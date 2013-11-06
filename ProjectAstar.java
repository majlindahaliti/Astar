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
public class ProjectAstar {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        Scanner in = new Scanner(System.in);
        int mode = in.nextInt();
        if(mode == 1){
            NormalAstarGraph graph = new NormalAstarGraph(in);
            graph.createCopy();
            int src,dst;
            src = in.nextInt();
            dst = in.nextInt();
            graph.bidirectionalAstar(src, dst);
        }
        else if(mode == 2){
            EightPuzzleGraph graph = new EightPuzzleGraph();
            ArrayList<ArrayList<Integer>> srcgrid = new ArrayList<ArrayList<Integer>>();
            ArrayList<ArrayList<Integer>> dstgrid = new ArrayList<ArrayList<Integer>>();
            for (int i = 0; i < 3; i++) {
                ArrayList<Integer> row = new ArrayList<Integer>();
                for (int j = 0; j < 3; j++) {
                    row.add(in.nextInt());
                }
                srcgrid.add(row);
            }
            for (int i = 0; i < 3; i++) {
                ArrayList<Integer> row = new ArrayList<Integer>();
                for (int j = 0; j < 3; j++) {
                    row.add(in.nextInt());
                }
                dstgrid.add(row);
            }
            graph.astarSearch(srcgrid, dstgrid);
        }
        else if(mode == 3){
            MCGraph graph = new MCGraph();
            int m = in.nextInt();
            int c = in.nextInt();
            int b = in.nextInt();
            graph.astarSearch(m, c, b);
        }
    }
}

