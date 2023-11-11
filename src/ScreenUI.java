import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.io.*;
import java.util.*;

public class ScreenUI implements ActionListener {
    JTextField input_text;
    public ScreenUI(){
    JFrame jf = new JFrame("Drone++");
    jf.setVisible(true);
    jf.setSize(1280,720);
    jf.setDefaultCloseOperation(jf.EXIT_ON_CLOSE);
    jf.setLocationRelativeTo(null);
    jf.setLayout(null);
    JButton jb1= new JButton("SET INPUT");
    JButton jb2= new JButton("GUIDE");
    jb1.setBounds(452, 249, 375, 91);
    jb1.setFont(new Font("Impact",Font.PLAIN,40));
    jb2.setBounds(452, 380, 375, 91);
    jb2.setFont(new Font("Impact",Font.PLAIN,40));
    jf.add(jb1);
    jf.add(jb2);
    jb1.addActionListener(this::inputUI);
    jb2.addActionListener(this);
    }
    
    public void inputUI(ActionEvent actionEvent){
        JFrame jf2 = new JFrame("Drone++");
        jf2.setVisible(true);
        jf2.setSize(1280,720);
        jf2.setLocationRelativeTo(null);
        jf2.setLayout(null);
        input_text = new JTextField("ENTER INPUT: N X1 Y1 X2 Y2 ... XN YN");
        JButton jb4= new JButton("CLICK HERE");
        input_text.setBounds(452, 249, 375, 91);
        input_text.setFont(new Font("Impact",Font.PLAIN,28));
        jb4.setBounds(452, 380, 375, 91);
        jb4.setFont(new Font("Impact",Font.PLAIN,40));
        jf2.add(input_text);
        jf2.add(jb4);
        jb4.addActionListener(this::outputMessage);

    }
    public static double dist(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    public void outputMessage(ActionEvent actionEvent) {
        String str = new String(input_text.getText());
        String[] args=str.split("\\s+");
        int n = Integer.parseInt(args[0]);
        double x[] = new double[n], y[] = new double[n];
        for(int i = 1; i < 2 * n + 1; i += 2) {
            x[(i - 1) / 2] = Double.parseDouble(args[i]);
            y[(i - 1) / 2] = Double.parseDouble(args[i + 1]);
        }
        // declaração das variáveis do TSP
        // dp -> melhor distância alcançada, utilizando o bitmask e a última coordenada alcançada.
        // ex.: bitmask -> 0001011, última coordenada -> 1
        // quer dizer que dp[1][11] = menor distância de se sair da origem, andar pelas coordenadas 0, 1, 3, e se ter a coordenada 1 como a última alcançada
        // prv -> guardar a transição da dp
        double dp[][] = new double[n][(1 << n)]; 
        int prv[][] = new int[n][(1 << n)];
        // definição de critérios iniciais
        for(int i = 0; i < n; i++) {
            dp[i][(1 << i)] = dist(0, 0, x[i], y[i]);
            prv[i][(1 << i)] = -1;
        }
        for(int mask = 1; mask < (1 << n); mask++) {
            for(int i = 0; i < n; i++) {
                for(int j = 0; j < n; j++) {
                    if(i == j) continue;
                    // faço a conferência de se o bit i está ativo e o j não
                    // caso isso for verdade, posso pensar na transição de ir do i para o j
                    if(((mask >> i) & 1) == 1 && ((mask >> j) & 1) == 0) {
                        // somente faço a transição se não tiver chegado naquele estado ou o valor naquele estado é pior do que posso alcançar agora
                        if(dp[j][(mask | (1 << j))] == 0 || dp[j][(mask | (1 << j))] > dp[i][mask] + dist(x[i], y[i], x[j], y[j])) {
                            dp[j][(mask | (1 << j))] = dp[i][mask] + dist(x[i], y[i], x[j], y[j]);
                            prv[j][(mask | (1 << j))] = i;
                        }
                    }
                }
            }
        }
        double tmp = Double.MAX_VALUE;
        int id = -1;
        // recuperção da resposta, precisamos também pensar na volta para a origem
        for(int i = 0; i < n; i++) {
            if(dp[i][(1 << n) - 1] + dist(0, 0, x[i], y[i]) < tmp) {
                tmp = dp[i][(1 << n) - 1] + dist(0, 0, x[i], y[i]);
                id = i;
            }
        }
        ArrayList<Integer> path = new ArrayList<Integer>();
        // recuperação do caminho, dado que sabemos a última coordenada alcançada, de lá podemos ir removendo bit a bit para encontrar o caminho baseado na matriz prv
        int mask = (1 << n) - 1;
        path.add(-1);
        while(id != -1) {
            path.add(id);
            int nid = prv[id][mask];
            mask ^= (1 << id);
            id = nid;
        }
        path.add(-1);
        // transformação da resposta encontrada em string para o programa.
        String output1 = new String(tmp + " is the total distance.");
        String output2 = new String("The order of the points:\n");
        for(int i = 0; i < path.size(); i++) {
            if(path.get(i) == -1) {
               output2+=" (0,0)\n";
            } 
            else {
                output2+=" " + "("+x[path.get(i)]+ "," + y[path.get(i)]+")\n";
            }
        }
        JOptionPane.showMessageDialog(null,output1);
        JOptionPane.showMessageDialog(null,output2);
    }

    public void actionPerformed(ActionEvent e) {
        JOptionPane.showMessageDialog(null,"Please go to 'Set Input' and Enter the coordinates that the drone should reach.\nThen, our app will give you the shortest path passing through all the points");
    }
    
}