

package zurrapa;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;



public class app_balcao {
    private Connect_ZurrapaBD connect;
    
    
    private int idmesa;
 
    public app_balcao(){
        try {
            Begin();
        } catch (SQLException ex) {
            Logger.getLogger(app_balcao.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
   
    private void Begin() throws SQLException {
        Scanner id_empregado = new Scanner(System.in);
        Scanner id_bar = new Scanner(System.in);
        Scanner aberto = new Scanner(System.in);
        Scanner id_pedido = new Scanner(System.in);
        
        int idempregado, idbar, pedidos, idpedido;
        
        boolean idEBcheck = false, idPedidocheck = false;
        
        ArrayList<String> pedidosAbertos = new ArrayList<>();
        
        connect = new Connect_ZurrapaBD(); // cria ligacao a BD
        
        System.out.println("*******************************");
        System.out.println("*Aplicação Empregado de Balcão*");
        System.out.println("*******************************");
        
        System.out.println("Insira o seu ID empregado:");
        idempregado = id_empregado.nextInt();
        System.out.println("Insira ID bar onde está a trabalhar:");
        idbar = id_bar.nextInt();
        
        idEBcheck = connect.checkIdEmpregadoBar(idempregado, idbar); // verifica se existe este empregado de mesa no bar onde esta a trabalhar
        
        if(idEBcheck){
            do{
                System.out.println("1 - Pedidos em Aberto:");
                System.out.println("0 - Fechar aplicação"); 
                System.out.println("***************************");
                do {
                    System.out.println("Introduza a sua opção: ");
                    pedidos = aberto.nextInt();
                    
                } while (pedidos < 0 || pedidos > 1);
                switch(pedidos){
                    case 1:
                        pedidosAbertos = connect.checkPedidos(idbar,"aberto");
                        if(pedidosAbertos.isEmpty()){
                            System.out.println("Não existem pedidos com o estado satisfeito.");
                        }else{
                            System.out.println("Pedidos Abertos:");
                            for(int i = 0; i< pedidosAbertos.size(); i+=6){
                                System.out.println("IDPedido:"+pedidosAbertos.get(i)+", Data:"+pedidosAbertos.get(i+1)+", IDMesa:"+pedidosAbertos.get(i+2)+", IDEmpregado:"+pedidosAbertos.get(i+3)+", Estado:"+pedidosAbertos.get(i+4)+", Quantidade:"+pedidosAbertos.get(i+5)+"");
                            }
                            System.out.println("");
                            System.out.println("Pertende preparar algum pedido?, se sim introduza o id do pedido caso contrario introduza 0.");
                            idpedido = id_pedido.nextInt();
                            idPedidocheck = connect.checkPedido(idpedido,idempregado,idbar,"aberto");
                            if(idPedidocheck && idpedido > 0){
                                connect.updateQuantProduto(idpedido); //atualiza o stock bar
                                connect.updateEstadoPedido(idpedido,"satisfeito"); // muda o estado para satisfeito 
                                System.out.println("Pedido preparado com sucesso.");
                            }else{
                                System.out.println("Pedido inserido não existe na BD!");
                            }
                        }
                        break;
                }
            }while (pedidos != 0); 
        
        }else{
            System.out.println("Empregado e bar introduzido não existe na BD!");
            Begin();
        }
    }
    
 
    
      public static void main(String[] args) {
//         TODO code application logic here
        new app_balcao();
    } 
}
