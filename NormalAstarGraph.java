/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package projectastar;

import java.util.*;
/**
 *
 * @author code
 */
public class NormalAstarGraph {
    LinkedList<NormalNode> vertex_list;
    LinkedList<NormalEdge> edge_list;
    PriorityQueue<NormalNode> open_list;
    ArrayList<NormalNode> closed_list;
    LinkedList<NormalNode> rev_vertex_list;
    LinkedList<NormalEdge> rev_edge_list;
    PriorityQueue<NormalNode> rev_open_list;
    ArrayList<NormalNode> rev_closed_list;
    
    NormalAstarGraph(Scanner in){
        vertex_list = new LinkedList<NormalNode>();
        edge_list = new LinkedList<NormalEdge>();
        int num_vertices;
        num_vertices = in.nextInt();
        for(int i=0;i<num_vertices;i++){
            NormalNode temp_node = new NormalNode(i,0);
            vertex_list.add(temp_node);
            System.out.println("Added node "+temp_node.id);
        }
        int a,b,wt;
        a = in.nextInt();
        int i=0;
        NormalEdge temp_edge;
        while(a!=-1){
            b = in.nextInt();
            wt = in.nextInt();
            if(a<0 || a>num_vertices-1 || b<0 || b>num_vertices-1){
                System.err.println("Vertices out of bound");
                return;
            }
            temp_edge = new NormalEdge(i,vertex_list.get(a),vertex_list.get(b),wt);
            edge_list.add(temp_edge);
            vertex_list.get(a).adj_edges.add(temp_edge);
            vertex_list.get(b).adj_edges.add(temp_edge);
            a = in.nextInt();
            i++;
        }
    }
    
    void createCopy(){
        rev_vertex_list = new LinkedList<NormalNode>();
        rev_edge_list = new LinkedList<NormalEdge>();
        
        for(int i=0;i<vertex_list.size();i++){
            NormalNode temp_node = new NormalNode(i,0);
            rev_vertex_list.add(temp_node);
            System.out.println("Added node "+temp_node.id);
        }
        
        for(int i=0;i<edge_list.size();i++){
            NormalEdge cur_edge = edge_list.get(i);
            NormalEdge temp_edge=new NormalEdge(i,rev_vertex_list.get(cur_edge.first.id),rev_vertex_list.get(cur_edge.second.id),cur_edge.weight);
            rev_edge_list.add(temp_edge);
            rev_vertex_list.get(cur_edge.first.id).adj_edges.add(temp_edge);
            rev_vertex_list.get(cur_edge.second.id).adj_edges.add(temp_edge);
            System.out.println("Added node "+i);
        }
        System.out.println("Created copy");
    }
    
    void display(){
        for(int i=0;i<vertex_list.size();i++){
            NormalNode temp_node = vertex_list.get(i);
            LinkedList<NormalEdge> temp_elist = temp_node.adj_edges;
            for(NormalEdge temp_edge : temp_elist){
                System.out.print(temp_node.id+" ");
                if(temp_node.id == temp_edge.first.id)System.out.print(temp_edge.second.id+" ");
                else System.out.print(temp_edge.first.id+ " ");
                System.out.println(temp_edge.weight);
            }
        }
    }
    
    void astarSearch(int src, int dst){
        if (src < 0 || src > vertex_list.size() - 1 || dst < 0 || dst > vertex_list.size() - 1) {
            System.err.println("Vertices out of bound");
            return;
        }
        if(src == dst){System.out.println("source = destination");return;}
        Comparator<NormalNode> comparator = new NormalNodeComparator();
        open_list = new PriorityQueue<NormalNode>(10,comparator);
        closed_list = new ArrayList<NormalNode>();
        vertex_list.get(src).g = 0;
        open_list.add(vertex_list.get(src));
        NormalNode transfer_node = null;
        while(!open_list.isEmpty() && (transfer_node == null || transfer_node.id != dst)){
            System.out.print("Closed list: ");
            for(NormalNode temp_node : closed_list){
                System.out.print(temp_node.id+",");
            }
            System.out.println();
            System.out.print("Open list: ");
            for(NormalNode temp_node : open_list){
                System.out.print(temp_node.id+",");
            }
            System.out.println();
            transfer_node = open_list.poll();
            transfer_node.open = 0;
            closed_list.add(transfer_node);
            for(NormalEdge temp_edge : transfer_node.adj_edges){
                NormalNode adj_node;
                if(temp_edge.first.id == transfer_node.id)adj_node = temp_edge.second;
                else adj_node = temp_edge.first;
                if(adj_node.open == -1){
                    adj_node.g = transfer_node.g+ temp_edge.weight;
                    adj_node.predecessor = transfer_node;
                    adj_node.open = 1;
                    open_list.add(adj_node);
                }
                else if(adj_node.open == 1){
                    if(transfer_node.g+temp_edge.weight < adj_node.g){
                        adj_node.g = transfer_node.g+temp_edge.weight;
                        adj_node.predecessor = transfer_node;
                    }
                }
                else if(adj_node.open == 0){
                    if (transfer_node.g + temp_edge.weight < adj_node.g) {
                        adj_node.g = transfer_node.g + temp_edge.weight;
                        adj_node.predecessor = transfer_node;
                        parentRedirection(adj_node);
                    }
                }
            }
        }
        System.out.print("Closed list: ");
        for (NormalNode temp_node : closed_list) {
            System.out.print(temp_node.id + ",");
        }
        System.out.println();
        System.out.print("Open list: ");
        for (NormalNode temp_node : open_list) {
            System.out.print(temp_node.id + ",");
        }
        System.out.println("\n");
        if(transfer_node.id == dst){System.out.println("Path:");transfer_node.printPath();}
    }
    
    void parentRedirection(NormalNode node){
        for(NormalEdge temp_edge : node.adj_edges){
            NormalNode adj_node;
            if(temp_edge.first.id == node.id)adj_node = temp_edge.second;
            else adj_node = temp_edge.first;
            if(adj_node.open == 0){
                if (node.g + temp_edge.weight < adj_node.g) {
                    adj_node.g = node.g + temp_edge.weight;
                    adj_node.predecessor = node;
                    parentRedirection(adj_node);
                }
            }
        }
    }
    
    void bidirectionalAstar(int src,int dst){
        if (src < 0 || src > vertex_list.size() - 1 || dst < 0 || dst > vertex_list.size() - 1) {
            System.err.println("Vertices out of bound");
            return;
        }
        if(src == dst){System.out.println("source = destination");return;}
        
        Comparator<NormalNode> comparator = new NormalNodeComparator();
        open_list = new PriorityQueue<NormalNode>(10,comparator);
        closed_list = new ArrayList<NormalNode>();
        rev_open_list = new PriorityQueue<NormalNode>(10,comparator);
        rev_closed_list = new ArrayList<NormalNode>();
        
        vertex_list.get(src).g = 0;
        open_list.add(vertex_list.get(src));
        rev_vertex_list.get(dst).g = 0;
        rev_open_list.add(rev_vertex_list.get(dst));
        
        NormalNode transfer_node = null;
        NormalNode rev_transfer_node = null;
        
        while(transfer_node==null || (transfer_node.id != dst && rev_transfer_node.id != src && transfer_node.id!=rev_transfer_node.id)){
            System.out.println("Forward search details:");
            System.out.print("Closed list: ");
            for(NormalNode temp_node : closed_list){
                System.out.print(temp_node.id+",");
            }
            System.out.println();
            System.out.print("Open list: ");
            for(NormalNode temp_node : open_list){
                System.out.print(temp_node.id+",");
            }
            System.out.println();
            
            System.out.println("Backward search details:");
            System.out.print("Closed list: ");
            for(NormalNode temp_node : rev_closed_list){
                System.out.print(temp_node.id+",");
            }
            System.out.println();
            System.out.print("Open list: ");
            for(NormalNode temp_node : rev_open_list){
                System.out.print(temp_node.id+",");
            }
            System.out.println("\n");
            
            transfer_node = open_list.poll();
            if(transfer_node!=null){
                transfer_node.open = 0;
                closed_list.add(transfer_node);
                for (NormalEdge temp_edge : transfer_node.adj_edges) {
                    NormalNode adj_node;
                    if (temp_edge.first.id == transfer_node.id) {
                        adj_node = temp_edge.second;
                    } else {
                        adj_node = temp_edge.first;
                    }
                    if (adj_node.open == -1) {
                        adj_node.g = transfer_node.g + temp_edge.weight;
                        adj_node.predecessor = transfer_node;
                        adj_node.open = 1;
                        open_list.add(adj_node);
                    } else if (adj_node.open == 1) {
                        if (transfer_node.g + temp_edge.weight < adj_node.g) {
                            adj_node.g = transfer_node.g + temp_edge.weight;
                            adj_node.predecessor = transfer_node;
                        }
                    } else if (adj_node.open == 0) {
                        if (transfer_node.g + temp_edge.weight < adj_node.g) {
                            adj_node.g = transfer_node.g + temp_edge.weight;
                            adj_node.predecessor = transfer_node;
                            parentRedirection(adj_node);
                        }
                    }
                }
            }
            
            rev_transfer_node = rev_open_list.poll();
            if(rev_transfer_node!=null){
                rev_transfer_node.open = 0;
                rev_closed_list.add(rev_transfer_node);
                for (NormalEdge temp_edge : rev_transfer_node.adj_edges) {
                    NormalNode adj_node;
                    if (temp_edge.first.id == rev_transfer_node.id) {
                        adj_node = temp_edge.second;
                    } else {
                        adj_node = temp_edge.first;
                    }
                    if (adj_node.open == -1) {
                        adj_node.g = rev_transfer_node.g + temp_edge.weight;
                        adj_node.predecessor = rev_transfer_node;
                        adj_node.open = 1;
                        rev_open_list.add(adj_node);
                    } else if (adj_node.open == 1) {
                        if (rev_transfer_node.g + temp_edge.weight < adj_node.g) {
                            adj_node.g = rev_transfer_node.g + temp_edge.weight;
                            adj_node.predecessor = rev_transfer_node;
                        }
                    } else if (adj_node.open == 0) {
                        if (rev_transfer_node.g + temp_edge.weight < adj_node.g) {
                            adj_node.g = rev_transfer_node.g + temp_edge.weight;
                            adj_node.predecessor = rev_transfer_node;
                            parentRedirection(adj_node);
                        }
                    }
                }
            }
            
            if(transfer_node==null && rev_transfer_node==null)break;
            
        }
        
        if(transfer_node==null && rev_transfer_node==null){System.err.println("unable to find path");return;}
        
        else if(transfer_node.id==dst){System.out.println("Path:");transfer_node.printPath();}
        
        else if(rev_transfer_node.id==src){System.out.println("Path:");rev_transfer_node.printPath();}
        
        else if(transfer_node.id==rev_transfer_node.id){
            System.out.println("Path:");
            transfer_node.printPath();
            rev_transfer_node.revPrintPath();
        }
    }
    
}


class NormalEdge {
    int id;
    int weight;
    NormalNode first, second;

    NormalEdge(int i, NormalNode a, NormalNode b, int wt) {
        id = i;
        first = a;
        second = b;
        weight = wt;
    }
}



class NormalNode {
    int id,visited;
    LinkedList<NormalEdge> adj_edges;
    NormalNode predecessor;
    int open;
    int g, h;

    NormalNode(int i, int v) {
        id = i;
        visited = v;
        open = -1;
        h = 0;
        predecessor = null;
        adj_edges = new LinkedList<NormalEdge>();
    }
    
    void printPath(){
        if(this.predecessor!=null)this.predecessor.printPath();
        System.out.println(this.id);
    }
    
    void revPrintPath(){
        System.out.println(this.id);
        if(this.predecessor!=null)this.predecessor.printPath();
    }
}


class NormalNodeComparator implements Comparator<NormalNode>{
    @Override
    public int compare(NormalNode x, NormalNode y){
        if(x.g + x.h < y.g + y.h)return -1;
        if(x.g + x.h > y.g + y.h)return 1;
        else return 0;
    }
}

