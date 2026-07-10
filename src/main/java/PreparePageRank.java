import it.unimi.dsi.webgraph.BVGraph;
import it.unimi.dsi.webgraph.ImmutableGraph;
import it.unimi.dsi.webgraph.NodeIterator;
import it.unimi.dsi.webgraph.Transform;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;

//mvn exec:java -Dexec.mainClass="PreparePageRank" -Dexec.args="eu-2005"

public class PreparePageRank {

    // Tamaño de lote para la transposición. Más grande = más rápido y más RAM.
    static final int BATCH = 10000000;

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Uso: java PreparePageRank <basename>");
            return;
        }

        // 1) grafo forward, sin self-loops
        ImmutableGraph g0 = BVGraph.load(args[0]);
        ImmutableGraph g  = Transform.filterArcs(g0, Transform.NO_LOOPS);
        int n = g.numNodes();

        // 2) col_count.bin: out-degree (forward, sin loops), en orden de nodo
        DataOutputStream cc = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(args[0] + ".col_count.bin")));
        NodeIterator git = g.nodeIterator();
        while (git.hasNext()) {
            git.nextInt();
            cc.writeInt(Integer.reverseBytes(git.outdegree()));   // little-endian
        }
        cc.close();

        // 3) transpuesta M = A^T -> .adj (la matriz que cargará PageRank)
        ImmutableGraph gt = Transform.transposeOffline(g, BATCH);

        RandomAccessFile out = new RandomAccessFile(args[0] + ".transpose.adj", "rw");
        out.writeInt(Integer.reverseBytes(n));
        out.writeLong(Long.reverseBytes(0L));     // placeholder de numArcs

        long arcs = 0;
        NodeIterator it = gt.nodeIterator();
        while (it.hasNext()) {
            int node = it.nextInt();
            int deg  = it.outdegree();
            int[] succ = it.successorArray();      // válido hasta el siguiente next
            out.writeInt(Integer.reverseBytes(-(node + 1)));       // cabecera del nodo
            for (int j = 0; j < deg; j++)
                out.writeInt(Integer.reverseBytes(1 + succ[j]));
            arcs += deg;
            if (node % 1000000 == 0) System.out.println("Nodes " + node);
        }

        out.seek(4);                               // reescribe numArcs real
        out.writeLong(Long.reverseBytes(arcs));
        out.close();

        System.out.println("Listo. Nodos=" + n + "  Arcos(sin loops)=" + arcs);
    }
}