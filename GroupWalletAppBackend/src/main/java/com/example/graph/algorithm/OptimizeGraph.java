package com.example.graph.algorithm;

import com.example.graph.model.Owes;
import com.example.graph.model.User;
import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;


import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class OptimizeGraph {
    private static final long OFFSET = 1000000000L;
    private static Set<Long> visitedEdges;

    public static List<OptimizedEdge> calculateOptimizedEdges(List<User> users) {
        List<NetworkFlowSolverBase.Edge> edges = new ArrayList<>();

        //assign labels and ids
        BidiMap<Long, Integer> mappedLabels = new DualHashBidiMap<>();

        int currentIndex = 0;

        for (User u : users) {
            mappedLabels.put(u.getId(), currentIndex++);
        }

        for (User u : users) {
            for (Owes o : u.getDebtors()) {
                User debtor = o.getDebtor();

                if (mappedLabels.containsKey(debtor.getId())) {
                    edges.add(new NetworkFlowSolverBase.Edge(
                            mappedLabels.get(debtor.getId()), mappedLabels.get(u.getId()), o.getDebt()));
                }
            }
        }

        String[] labels = mappedLabels.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue())
                        .map(e -> e.getKey().toString())
                        .toArray(String[]::new);

        Dinics solver = createGraphForDebts(edges, labels);

        return solver.edges.stream()
                .map(e -> new OptimizedEdge(mappedLabels.getKey(e.to), mappedLabels.getKey(e.from), e.capacity))
                .collect(Collectors.toList());
    }

    private static Dinics createGraphForDebts(List<NetworkFlowSolverBase.Edge> edges, String[] labels) {
        int n = labels.length;
        Dinics solver = new Dinics(n, labels);
        solver = addAllTransactions(solver, edges);
        visitedEdges = new HashSet<>();
        Integer edgePos;

        while((edgePos = getNonVisitedEdge(solver.getEdges())) != null) {
            solver.recompute();
            Dinics.Edge firstEdge = solver.getEdges().get(edgePos);
            solver.setSource(firstEdge.from);
            solver.setSink(firstEdge.to);
            List<Dinics.Edge>[] residualGraph = solver.getGraph();
            List<Dinics.Edge> newEdges = new ArrayList<>();

            for(List<Dinics.Edge> allEdges : residualGraph) {
                for(Dinics.Edge edge : allEdges) {
                    BigDecimal remainingFlow = ((edge.flow.compareTo(BigDecimal.ZERO) < 0) ? edge.capacity : (edge.capacity.subtract(edge.flow)));
                    if(remainingFlow.compareTo(BigDecimal.ZERO) > 0) {
                        newEdges.add(new Dinics.Edge(edge.from, edge.to, remainingFlow));
                    }
                }
            }
            BigDecimal maxFlow = solver.getMaxFlow();
            int source = solver.getSource();
            int sink = solver.getSink();
            visitedEdges.add(getHashKeyForEdge(source, sink));
            solver = new Dinics(n, labels);
            solver.addEdges(newEdges);
            solver.addEdge(source, sink, maxFlow);
        }
        return solver;
    }

    private static Dinics addAllTransactions(Dinics solver, List<NetworkFlowSolverBase.Edge> edges) {
        solver.addEdges(edges);
        return solver;
    }

    private static Integer getNonVisitedEdge(List<Dinics.Edge> edges) {
        Integer edgePos = null;
        int curEdge = 0;
        for(Dinics.Edge edge : edges) {
            if(!visitedEdges.contains(getHashKeyForEdge(edge.from, edge.to))) {
                edgePos = curEdge;
            }
            curEdge++;
        }
        return edgePos;
    }

    private static Long getHashKeyForEdge(int u, int v) {
        return u * OFFSET + v;
    }
}

class Dinics extends NetworkFlowSolverBase {

    private int[] level;

    public Dinics(int n, String[] vertexLabels) {
        super(n, vertexLabels);
        level = new int[n];
    }

    @Override
    public void solve() {
        int[] next = new int[n];

        while (bfs()) {
            Arrays.fill(next, 0);
            for (BigDecimal f = dfs(s, next, INF); !f.equals(BigDecimal.ZERO); f = dfs(s, next, INF)) {
                maxFlow = maxFlow.add(f);
            }
        }
        for (int i = 0; i < n; i++) if (level[i] != -1) minCut[i] = true;
    }

    private boolean bfs() {
        Arrays.fill(level, -1);
        level[s] = 0;
        Deque<Integer> q = new ArrayDeque<>(n);
        q.offer(s);
        while (!q.isEmpty()) {
            int node = q.poll();
            for (Edge edge : graph[node]) {
                BigDecimal cap = edge.remainingCapacity();
                if (cap.compareTo(BigDecimal.ZERO) > 0 && level[edge.to] == -1) {
                    level[edge.to] = level[node] + 1;
                    q.offer(edge.to);
                }
            }
        }
        return level[t] != -1;
    }

    private BigDecimal dfs(int at, int[] next, BigDecimal flow) {
        if (at == t) return flow;
        final int numEdges = graph[at].size();

        for (; next[at] < numEdges; next[at]++) {
            Edge edge = graph[at].get(next[at]);
            BigDecimal cap = edge.remainingCapacity();
            if (cap.compareTo(BigDecimal.ZERO) > 0 && level[edge.to] == level[at] + 1) {

                BigDecimal bottleNeck = dfs(edge.to, next, flow.min(cap));
                if (bottleNeck.compareTo(BigDecimal.ZERO) > 0) {
                    edge.augment(bottleNeck);
                    return bottleNeck;
                }
            }
        }
        return BigDecimal.ZERO;
    }
}


abstract class NetworkFlowSolverBase {

    protected static final BigDecimal INF = BigDecimal.valueOf(Double.MAX_VALUE);

    public static class Edge {
        public int from, to;
        public Edge residual;
        public BigDecimal cost;
        public BigDecimal flow = BigDecimal.ZERO;
        public final BigDecimal capacity;
        public final BigDecimal originalCost;

        public Edge(int from, int to, BigDecimal capacity) {
            this(from, to, capacity, BigDecimal.ZERO /* unused */);
        }

        public Edge(int from, int to, BigDecimal capacity, BigDecimal cost) {
            this.from = from;
            this.to = to;
            this.capacity = capacity;
            this.originalCost = this.cost = cost;
        }

        public BigDecimal remainingCapacity() {
            return capacity.subtract(flow);
        }

        public void augment(BigDecimal bottleNeck) {
            flow = flow.add(bottleNeck);
            residual.flow = residual.flow.subtract(bottleNeck);
        }
    }

    protected int n, s, t;

    protected BigDecimal maxFlow = BigDecimal.ZERO;

    protected boolean[] minCut;
    protected List<Edge>[] graph;
    protected String[] vertexLabels;
    protected List<Edge> edges;

    private int[] visited;

    protected boolean solved;

    public NetworkFlowSolverBase(int n, String[] vertexLabels) {
        this.n = n;
        initializeGraph();
        assignLabelsToVertices(vertexLabels);
        minCut = new boolean[n];
        visited = new int[n];
        edges = new ArrayList<>();
    }

    private void assignLabelsToVertices(String[] vertexLabels) {
        if(vertexLabels.length != n)
            throw new IllegalArgumentException(String.format("You must pass %s number of labels", n));
        this.vertexLabels = vertexLabels;
    }

    private void initializeGraph() {
        graph = new List[n];
        for (int i = 0; i < n; i++) graph[i] = new ArrayList<Edge>();
    }

    public void addEdges(List<Edge> edges) {
        if (edges == null) throw new IllegalArgumentException("Edges cannot be null");
        for(Edge edge : edges) {
            addEdge(edge.from, edge.to, edge.capacity);
        }
    }

    public void addEdge(int from, int to, BigDecimal capacity) {
        if (capacity.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("Capacity < 0");
        Edge e1 = new Edge(from, to, capacity);
        Edge e2 = new Edge(to, from, BigDecimal.ZERO);
        e1.residual = e2;
        e2.residual = e1;
        graph[from].add(e1);
        graph[to].add(e2);
        edges.add(e1);
    }

    public List<Edge>[] getGraph() {
        execute();
        return graph;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public BigDecimal getMaxFlow() {
        execute();
        return maxFlow;
    }

    public void setSource(int s) {
        this.s = s;
    }

    public void setSink(int t) {
        this.t = t;
    }

    public int getSource() {
        return s;
    }

    public int getSink() {
        return t;
    }

    public void recompute() {
        solved = false;
    }

    private void execute() {
        if (solved) return;
        solved = true;
        solve();
    }

    public abstract void solve();
}
