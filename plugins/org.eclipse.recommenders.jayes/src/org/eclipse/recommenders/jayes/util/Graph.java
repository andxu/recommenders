/**
 * Copyright (c) 2011 Michael Kutschke.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Michael Kutschke - initial API and implementation.
 */
package org.eclipse.recommenders.jayes.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * undirected graph
 */
public class Graph implements Cloneable {

    // we need to remove edges a lot, thats why we use Set here instead of List
    private List<HashSet<Edge>> adjacency = new ArrayList<HashSet<Edge>>();

    public List<? extends Set<Edge>> getAdjacency() {
        return adjacency;
    }

    public void initialize(final int nodes) {
        adjacency.clear();
        for (int i = 0; i < nodes; i++) {
            adjacency.add(new HashSet<Edge>());
        }
    }

    public Edge addEdge(final int v1, final int v2) {
        for (int i = 0; i < Math.max(v1, v2) - adjacency.size(); i++) {
            adjacency.add(new HashSet<Edge>());
        }
        final Edge e = new Edge(v1, v2);
        adjacency.get(v1).add(e);
        adjacency.get(v2).add(e.initializeBackEdge());
        return e;
    }

    public void removeEdge(final Edge e) {
        adjacency.get(e.getFirst()).remove(e);
        adjacency.get(e.getSecond()).remove(e.getBackEdge());
    }

    public Set<Edge> getIncidentEdges(final int v) {
        return adjacency.get(v);
    }

    public List<Integer> getNeighbors(int var) {
        Set<Edge> incidentEdges = getIncidentEdges(var);
        List<Integer> elementNeighbors = new ArrayList<Integer>(incidentEdges.size());
        for (Edge e : incidentEdges) {
            elementNeighbors.add(e.getSecond());
        }
        return elementNeighbors;
    }

    public static class Edge extends OrderIgnoringPair<Integer> {

        private Edge backEdge;

        public Edge(final Integer o1, final Integer o2) {
            super(o1, o2);
        }

        public Edge initializeBackEdge() {
            backEdge = new Edge(getSecond(), getFirst());
            backEdge.backEdge = this;
            return getBackEdge();
        }

        public Edge getBackEdge() {
            return backEdge;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Graph clone() {
        try {
            Graph clone = (Graph) super.clone();
            clone.adjacency = new ArrayList<HashSet<Edge>>(adjacency.size());
            for (HashSet<Edge> edges : adjacency) {
                clone.adjacency.add((HashSet<Edge>) edges.clone());
            }
            return clone;
        } catch (CloneNotSupportedException e) {
            // should not happen
            throw new AssertionError(e.getMessage());
        }
    }
}
