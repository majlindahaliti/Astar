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
public class EightPuzzleGraph {
    LinkedList<EightPuzzleNode> vertex_list;
    PriorityQueue<EightPuzzleNode> open_list;
    HashSet<EightPuzzleNode> closed_list;
        
    
    EightPuzzleGraph(){
        vertex_list = new LinkedList<EightPuzzleNode>();
    }
    
    /*void display(){
        for(int i=0;i<vertex_list.size();i++){
            Node temp_node = vertex_list.get(i);
            LinkedList<Edge> temp_elist = temp_node.adj_edges;
            for(Edge temp_edge : temp_elist){
                System.out.print(temp_node.id+" ");
                if(temp_node.id == temp_edge.first.id)System.out.print(temp_edge.second.id+" ");
                else System.out.print(temp_edge.first.id+ " ");
                System.out.println(temp_edge.weight);
            }
        }
    }*/
    
    void astarSearch(ArrayList<ArrayList<Integer>> src, ArrayList<ArrayList<Integer>> dst){
        int siddu_steps=0;
        if(src.size()!=3 || dst.size()!=3){System.err.println("Array sizes not correct");return;}
        boolean[] srccheck=new boolean[9];
        boolean[] dstcheck=new boolean[9];
        Iterator it1 = src.iterator(), it2 = dst.iterator();
        while(it1.hasNext() && it2.hasNext()){
            ArrayList<Integer> rowsrc = (ArrayList<Integer>)it1.next();
            ArrayList<Integer> rowdst = (ArrayList<Integer>)it2.next();
            if(rowsrc.size()!=3 || rowdst.size()!=3){System.err.println("Array sizes not correct");return;}
            Iterator it3 = rowsrc.iterator(), it4 = rowdst.iterator();
            while(it3.hasNext() && it4.hasNext()){
                int elemsrc = (Integer)it3.next();
                int elemdst = (Integer)it4.next();
                //System.out.println(elemsrc+" "+elemdst);
                if(elemsrc<0 || elemsrc>8 || elemdst<0 || elemdst>8){System.err.println("Array elements out of bound");return;}
                srccheck[elemsrc]=true;
                dstcheck[elemdst]=true;
            }
        }
        boolean check=true;
        for(int i=0;i<9;i++)check = check && srccheck[i] && dstcheck[i];
        if(!check){System.err.println("Array elements do not satisfy 8 puzzle grid");return;}
        
        if(src.equals(dst)){System.out.println("source = destination");return;}
        Comparator<EightPuzzleNode> comparator = new EightPuzzleNodeComparator();
        open_list = new PriorityQueue<EightPuzzleNode>(10,comparator);
        closed_list = new HashSet<EightPuzzleNode>();
        EightPuzzleNode start = new EightPuzzleNode(src,dst);
        start.g = 0;
        open_list.add(start);
        EightPuzzleNode transfer_node = null;
        
        while(!open_list.isEmpty() && (transfer_node == null || !transfer_node.grid.equals(dst))){
            siddu_steps++;
           /* System.out.print("Open list: ");
            for(EightPuzzleNode temp_node : open_list){
                System.out.print(temp_node.grid.toString());
            }
            System.out.println();
            System.out.print("Closed list: ");
            for(EightPuzzleNode temp_node : closed_list){
                System.out.print(temp_node.grid.toString());
            }
            System.out.println("\n");*/
            
            transfer_node = open_list.poll();
            transfer_node.open = 0;
            closed_list.add(transfer_node);
            ArrayList<EightPuzzleNode> adj_nodes = transfer_node.getAdjacentNodes(dst);
            for(EightPuzzleNode adj_node : adj_nodes){
                Iterator it = closed_list.iterator();
                boolean inCL=false;
                while(it.hasNext()){
                    EightPuzzleNode closed_node = (EightPuzzleNode)it.next();
                    if(adj_node.equals(closed_node)){
                        if (transfer_node.g + 1 < closed_node.g) {
                            closed_node.g = transfer_node.g + 1;
                            closed_node.predecessor = transfer_node;
                            parentRedirection(closed_node,dst);
                        }
                        inCL = true;break;
                    }
                }
                if(inCL)continue;
                it = open_list.iterator();
                boolean inOL=false;
                while(it.hasNext()){
                    EightPuzzleNode open_node = (EightPuzzleNode)it.next();
                    if(adj_node.equals(open_node)){
                        if (transfer_node.g + 1 < open_node.g) {
                            open_node.g = transfer_node.g + 1;
                            open_node.predecessor = transfer_node;
                        }
                        inOL = true;
                        break;
                    }
                }
                if(inOL)continue;
                adj_node.g = transfer_node.g + 1;
                adj_node.predecessor = transfer_node;
                adj_node.open = 1;
                open_list.add(adj_node);
            }
        }
        
       /* System.out.print("Open list: ");
        for (EightPuzzleNode temp_node : open_list) {
            System.out.print(temp_node.grid.toString());
        }
        System.out.println();
        System.out.print("Closed list: ");
        for (EightPuzzleNode temp_node : closed_list) {
            System.out.print(temp_node.grid.toString());
        }
        System.out.println("\n");*/
        System.out.println(siddu_steps);
        
        if(transfer_node.grid.equals(dst)){System.out.println("Path:");transfer_node.printPath();}
    }
    
    void parentRedirection(EightPuzzleNode node, ArrayList<ArrayList<Integer>> dst){
        ArrayList<EightPuzzleNode> adj_nodes = node.getAdjacentNodes(dst);
        for(EightPuzzleNode adj_node : adj_nodes){
            Iterator it = closed_list.iterator();
            boolean inCL = false;
            while (it.hasNext()) {
                EightPuzzleNode closed_node = (EightPuzzleNode) it.next();
                if (adj_node.equals(closed_node)) {
                    if (node.g + 1 < closed_node.g) {
                        closed_node.g = node.g + 1;
                        closed_node.predecessor = node;
                        parentRedirection(closed_node,dst);
                    }
                    inCL = true;
                    break;
                }
            }
            if (inCL)continue;
        }
    }
}


class EightPuzzleNode {
    ArrayList<ArrayList<Integer>> grid;
    EightPuzzleNode predecessor;
    int open;
    int g, h;
    int rowzero,colzero;

    EightPuzzleNode(ArrayList<ArrayList<Integer>> i, ArrayList<ArrayList<Integer>> dst) {
        grid = i;
        open = -1;
        h = 0;
        predecessor = null;
        Iterator it1 = i.iterator(), it2 = dst.iterator();
        int rz=0;
        while(it1.hasNext() && it2.hasNext()){
            ArrayList<Integer> rowi = (ArrayList<Integer>)it1.next();
            ArrayList<Integer> rowdst = (ArrayList<Integer>)it2.next();
            Iterator it3 = rowi.iterator(), it4 = rowdst.iterator();
            int cz=0;
            while(it3.hasNext() && it4.hasNext()){
                int elemi = (Integer)it3.next();
                int elemdst = (Integer)it4.next();
                if(elemi != elemdst)h++;
                if(elemi==0){rowzero = rz; colzero = cz;}
                cz++;
            }
            rz++;
        }
        
        it1 = i.iterator(); it2 = dst.iterator();
        int posx=0,posy=0;
        int[] siddu_set1x = new int[9];
        int[] siddu_set1y = new int[9];
        int[] siddu_set2x = new int[9];
        int[] siddu_set2y = new int[9];
            
        while(it1.hasNext() && it2.hasNext()){
            ArrayList<Integer> rowi = (ArrayList<Integer>)it1.next();
            ArrayList<Integer> rowdst = (ArrayList<Integer>)it2.next();
            Iterator it3 = rowi.iterator(), it4 = rowdst.iterator();
            while(it3.hasNext() && it4.hasNext()){
                int elemi = (Integer)it3.next();
                int elemdst = (Integer)it4.next();
                siddu_set1x[elemi]=posx;
                siddu_set1y[elemi]=posy;
                siddu_set2x[elemdst]=posx;
                siddu_set2y[elemdst]=posy;
                posy++;
            }
            posx++;
            posy=0;
        }
        int id=1;int mh=0;
        while(id!=9){
            int x=Math.abs(siddu_set1x[id]-siddu_set2x[id]);
            int y=Math.abs(siddu_set1y[id]-siddu_set2y[id]);
            int z=(int) Math.sqrt(x*x+y*y);
            //mh+=z;
            mh+=x+y+z*z;
            //mh+=0.5;
            id++;
        }
        h=mh;
    }
    
    
    boolean equals(EightPuzzleNode dst){
        Iterator it1 = this.grid.iterator(), it2 = dst.grid.iterator();
        while(it1.hasNext() && it2.hasNext()){
            ArrayList<Integer> rowi = (ArrayList<Integer>)it1.next();
            ArrayList<Integer> rowdst = (ArrayList<Integer>)it2.next();
            Iterator it3 = rowi.iterator(), it4 = rowdst.iterator();
            while(it3.hasNext() && it4.hasNext()){
                int elemi = (Integer)it3.next();
                int elemdst = (Integer)it4.next();
                if(elemi != elemdst)return false;
            }
        }
        return true;
    }
    
    ArrayList<EightPuzzleNode> getAdjacentNodes(ArrayList<ArrayList<Integer>> dst){
        ArrayList<EightPuzzleNode> adj_nodes=new ArrayList<EightPuzzleNode>();
        int rz = rowzero-1;
        if(rz>=0 && rz<=2){
            ArrayList<ArrayList<Integer>> new_grid = new ArrayList<ArrayList<Integer>>();
            for(ArrayList<Integer> row : grid)new_grid.add((ArrayList<Integer>)row.clone());
            new_grid.get(rowzero).set(colzero,new_grid.get(rz).get(colzero));
            new_grid.get(rz).set(colzero,0);
            EightPuzzleNode node = new EightPuzzleNode (new_grid, dst);
            adj_nodes.add(node);
        }
        rz = rowzero+1;
        if(rz>=0 && rz<=2){
            ArrayList<ArrayList<Integer>> new_grid = new ArrayList<ArrayList<Integer>>();
            for(ArrayList<Integer> row : grid)new_grid.add((ArrayList<Integer>)row.clone());
            new_grid.get(rowzero).set(colzero,new_grid.get(rz).get(colzero));
            new_grid.get(rz).set(colzero,0);
            EightPuzzleNode node = new EightPuzzleNode (new_grid, dst);
            adj_nodes.add(node);
        }
        int cz = colzero-1;
        if(cz>=0 && cz<=2){
            ArrayList<ArrayList<Integer>> new_grid = new ArrayList<ArrayList<Integer>>();
            for(ArrayList<Integer> row : grid)new_grid.add((ArrayList<Integer>)row.clone());
            new_grid.get(rowzero).set(colzero,new_grid.get(rowzero).get(cz));
            new_grid.get(rowzero).set(cz,0);
            EightPuzzleNode node = new EightPuzzleNode (new_grid, dst);
            adj_nodes.add(node);
        }
        cz = colzero+1;
        if(cz>=0 && cz<=2){
            ArrayList<ArrayList<Integer>> new_grid = new ArrayList<ArrayList<Integer>>();
            for(ArrayList<Integer> row : grid)new_grid.add((ArrayList<Integer>)row.clone());
            new_grid.get(rowzero).set(colzero,new_grid.get(rowzero).get(cz));
            new_grid.get(rowzero).set(cz,0);
            EightPuzzleNode node = new EightPuzzleNode (new_grid, dst);
            adj_nodes.add(node);
        }
        return adj_nodes;
    }
    
    void printPath(){
        if(this.predecessor!=null)this.predecessor.printPath();
        System.out.println(this.grid.toString());
    }
}


class EightPuzzleNodeComparator implements Comparator<EightPuzzleNode>{
    @Override
    public int compare(EightPuzzleNode x, EightPuzzleNode y){
        if(x.g + x.h < y.g + y.h)return -1;
        if(x.g + x.h > y.g + y.h)return 1;
        else return 0;
    }
}


