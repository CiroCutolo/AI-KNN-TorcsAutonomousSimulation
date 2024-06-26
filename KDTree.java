package scr;

import java.util.*;

class KDTree {

    private final KDNode root;
    private final int dim; // Number of dimensions

    public KDTree(List<TrainingData> points) {
        this.dim = 10;
        root = buildTree(points, 0);
    }

    private static class KDNode {
        TrainingData point;
        KDNode left, right;

        KDNode(TrainingData point) {
            this.point = point;
        }
    }

    private KDNode buildTree(List<TrainingData> points, int depth) {
        if (points.isEmpty()) {
            return null;
        }

        int axis = depth % dim; // Cycle through axes
        points.sort(Comparator.comparingDouble(p -> p.getCoordinate(axis)));
        int medianIndex = points.size() / 2;
        KDNode node = new KDNode(points.get(medianIndex));

        node.left = buildTree(new ArrayList<>(points.subList(0, medianIndex)), depth + 1);
        node.right = buildTree(new ArrayList<>(points.subList(medianIndex + 1, points.size())), depth + 1);

        return node;
    }

    public List<TrainingData> kNearestNeighbors(TrainingData target, int k) {
        if (k <= 0) {
            throw new IllegalArgumentException("k must be greater than 0");
        }
        PriorityQueue<TrainingData> pq = new PriorityQueue<>(k, Comparator.comparingDouble(target::distance).reversed());
        kNearestNeighbors(root, target, k, 0, pq);
        List<TrainingData> result = new ArrayList<>(pq);
        result.sort(Comparator.comparingDouble(target::distance));
        return result;
    }

    private void kNearestNeighbors(KDNode node, TrainingData target, int k, int depth, PriorityQueue<TrainingData> pq) {
        if (node == null) {
            return;
        }

        double distance = target.distance(node.point);
        if (pq.size() < k) {
            pq.offer(node.point);
        } else if (distance < target.distance(pq.peek())) {
            pq.poll();
            pq.offer(node.point);
        }

        int axis = depth % this.dim;
        KDNode nearNode = (target.getCoordinate(axis) < node.point.getCoordinate(axis)) ? node.left : node.right;
        KDNode farNode = (nearNode == node.left) ? node.right : node.left;

        kNearestNeighbors(nearNode, target, k, depth + 1, pq);

        if (pq.size() < k || Math.abs(target.getCoordinate(axis) - node.point.getCoordinate(axis)) < target.distance(pq.peek())) {
            kNearestNeighbors(farNode, target, k, depth + 1, pq);
        }
    }
}





