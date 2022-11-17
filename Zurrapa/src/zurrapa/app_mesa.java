
package zurrapa;

import com.mysql.jdbc.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;



public class app_mesa {
    private Connect_ZurrapaBD connect;
    
    public app_mesa(){
        try {
            Begin();
        } catch (SQLException ex) {
            Logger.getLogger(app_mesa.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void Begin() throws SQLException {
        Scanner id_empregado = new Scanner(System.in);
        Scanner id_bar = new Scanner(System.in);
        Scanner menu_mesa = new Scanner(System.in);
        int idempregado, idbar, menu;
        
        boolean idEBcheck = false;
        
        connect = new Connect_ZurrapaBD(); // cria ligacao a BD
        
        System.out.println("*****************************");
        System.out.println("*Aplicação Empregado de Mesa*");
        System.out.println("*****************************");
        
        
        System.out.println("Insira o seu ID empregado:");
        idempregado = id_empregado.nextInt();
        System.out.println("Insira ID bar onde está a trabalhar:");
        idbar = id_bar.nextInt();
        
        idEBcheck = connect.checkIdEmpregadoBar(idempregado, idbar); // verifica se existe este empregado de mesa no bar onde esta a trabalhar
        
        if(idEBcheck){
            do{
                System.out.println("***********Menu*******************");
                System.out.println("1 - Mesas disponíveis para servir:");
                System.out.println("2 - Pedidos por fechar:");
                System.out.println("3 - Mesas por fechar:");
                System.out.println("0 - Fechar aplicação");
                System.out.println("**********************************");
                do {
                    System.out.println("Introduza a sua opção: ");
                    menu = menu_mesa.nextInt();
                } while (menu < 0 || menu > 3);
                switch(menu){
                    case 1:
                        MesasDisponiveis(idbar, idempregado); //chama  a funçao que mostra as mesas disponiveis para servir 
                        break;
                    case 2: 
                        PedidosPorFechar(idbar, idempregado);
                        break;
                    case 3: 
                        MesasPorFechar(idbar, idempregado);
                        break;    
                        
                }
            }while (menu != 0);   
        }else{
            System.out.println("Empregado e bar introduzido não existe na BD!");
            Begin();
        }
    }
    
    private void MesasDisponiveis(int idbar, int idempregado) throws SQLException{
        ArrayList<Integer> listIdMesa = new ArrayList<>(); //lista para guardar todas as mesas disponiveis para servir
        
        Scanner id_mesa = new Scanner(System.in);
        int idmesa;
        
        boolean checkmesa =  false;
        
        listIdMesa = connect.checkMesaLivre(idbar, idempregado);
        if(listIdMesa.isEmpty()){
            System.out.println("Não existe mesas disponiveis.");
        }else{
            for(int i=0; i < listIdMesa.size(); i++){
            System.out.println("Mesa numero: "+listIdMesa.get(i));
            }
            System.out.println("***************************************");
            System.out.println("Escolha número da mesa que quer servir:");
            idmesa = id_mesa.nextInt();

            checkmesa = connect.checkMesa(idbar, idmesa, idempregado); // verifica se a mesa introduzia é valia para servir

            if(checkmesa){
                 Pedido(idbar, idempregado, idmesa);
            }else{
                 System.out.println("Mesa introduzida não está correcta!");
            }
        }
    }
    private void Pedido(int idbar, int idemp, int idmesa) throws SQLException{
        Scanner id_menu = new Scanner(System.in);
        Scanner produto = new Scanner(System.in);
        Scanner produtoquant = new Scanner(System.in);
        int menu, idproduto, quantproduto_temp, quantproduto_final, quantproduto_armazem;
        String nome_produto;
        
        ArrayList<Integer> listIdprodutos = new ArrayList<>();
        ArrayList<String> listprodutos = new ArrayList<>();
        
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); // para mais tarde ir buscar a data e hora do pedido
        Date date = new Date(); 
        do{
                System.out.println("********Menu Pedido********");
                System.out.println("1 - Novo Pedido");
                System.out.println("0 - Voltar atrás");
                System.out.println("***************************");
                do {
                    System.out.println("Introduza a sua opcao: ");
                    menu = id_menu.nextInt();
                } while (menu < 0 || menu > 1);
                switch(menu){
                    case 1:
                            listIdprodutos = connect.checkIDProduto(idbar);
                        
                        for(int i = 0; i< listIdprodutos.size();i++){ // lista os produtos existentes no bar
                            idproduto = listIdprodutos.get(i);  // busca o id produto
                            listprodutos = connect.checkProduto(idproduto); //busca os valores do produto
                            
                            for(int j = 0; j < listprodutos.size();j+=3){
                                System.out.println("ID produto:"+listprodutos.get(j)+", Nome:"+listprodutos.get(j+1)+", Designacao:"+listprodutos.get(j+2)+", Preco:"+listprodutos.get(j+3)+"€");
                                listprodutos.clear();
                            }
                        }
                        System.out.println("Introduza o id produto para efectuar pedido: ");
                        idproduto = produto.nextInt();
                        
                        System.out.println("Introduza a quantidade: ");
                        quantproduto_temp  = produtoquant.nextInt();
                        quantproduto_final = connect.checkQuantidadeBar(idproduto) - quantproduto_temp;// busca quantidade da tabela e verifica se é possivel vender
                        if(quantproduto_final >= 0){
                            connect.insertPedido(dateFormat.format(date), idmesa, idemp, idbar, "aberto", quantproduto_temp, idproduto); //inser pedido
                            //connect.updateQuantProduto(idproduto, quantproduto_final); // coloca a nova quantidade
                            connect.updateMesa(idmesa, idemp);//atualiza o estado da mesa para o id empregado e assim só ele pode atender.
                            System.out.println("Pedido inserido com sucesso");
                        }else{
                            //ir a tabela produto e buscar o stock armazem e avisar o cliente
                            quantproduto_armazem = connect.checkQuantidadeArmazem(idproduto);
                            if(quantproduto_armazem > 0){
                                System.out.println("Aguarde 5 min, Existe no stock do armazem("+quantproduto_armazem+" paletes)!");
                                System.out.println("Pedido não efetuado.");
                            }else{
                                System.out.println("Stock indisponivel no armazem e no bar!");
                            }
                            
                        }
                        break;
                }
            }while (menu != 0); 
        
        
    }
    private void MesasPorFechar(int idbar, int idemp) throws SQLException{
        Scanner idmenu = new Scanner(System.in);
        Scanner idmesa = new Scanner(System.in);
        int menu, mesa = 0;
        
        Boolean checkmesa = false;
        
        ArrayList<Integer> listminhasmesas = new ArrayList<>();
        do{
                System.out.println("*********Menu Mesa*********");
                System.out.println("1 - As minhas mesas");
                System.out.println("0 - Voltar atrás");
                System.out.println("***************************");
                do {
                    System.out.println("Introduza a sua opcao: ");
                    menu = idmenu.nextInt();
                } while (menu < 0 || menu > 1);
                switch(menu){
                    case 1:
                        listminhasmesas = connect.checkMinhaMesa(idbar, idemp); //guarda para uma lista todas as mesas que iniciei pedidos
                        for(int i = 0; i< listminhasmesas.size(); i++){
                            System.out.println("Mesa por fechar:"+listminhasmesas.get(i));
                        }
                        if(listminhasmesas.isEmpty()){ // verifica se a lista esta vazia, se sim nao ha mesas para fechar
                            System.out.println("Não existem mesas para fechar.");
                            menu = 0; // volta atras nos menus sem pedir ao utilizador visto que nao ha mesas.
                        }else{
                            System.out.println("Pertende fechar alguma mesa?, se sim introduza o id da mesa caso contrario introduza 0.");
                            mesa = idmesa.nextInt();
                            checkmesa = connect.checkMesa(idbar, mesa, idemp);//verifico se a mesa é mesmo daquele empregado e daquele bar
                            if(checkmesa && mesa >0){
                                connect.updateMesaFechada(mesa); // atualiza o estado da mesa para null para outros empregados puderem utilizar
                                System.out.println("Mesa fechada com exito.");
                            }else if(mesa == 0){
                                menu = 0; //volta atras no menu
                            }else{
                                System.out.println("Mesa introduzida invalida e não foi fechada.");
                            }
                        }
                        break;
                }
            }while (menu != 0); 
    }
    
    
     private void PedidosPorFechar(int idbar, int idempregado) throws SQLException {
        
        Scanner satis = new Scanner(System.in);
        Scanner id_pedido = new Scanner(System.in);
        
        int pedidos, idpedido;
        Boolean idPedidocheck = false;
        
        ArrayList<String> pedidosSatis = new ArrayList<>();
        
        do{
            System.out.println("1 - Pedidos Abertos:");
            System.out.println("0 - Fechar aplicação"); 
            System.out.println("***************************");
            do {
                System.out.println("Introduza a sua opção: ");
                pedidos = satis.nextInt();

            } while (pedidos < 0 || pedidos > 1);
            switch(pedidos){
                case 1:
                    pedidosSatis = connect.checkPedidos(idbar,"satisfeito");
                    if(pedidosSatis.isEmpty()){
                        //cftvbgyhnu
                    }
                    System.out.println("Pedidos por Satisfazer:");
                    for(int i = 0; i< pedidosSatis.size(); i+=6){
                        System.out.println("IDPedido:"+pedidosSatis.get(i)+", Data:"+pedidosSatis.get(i+1)+", IDMesa:"+pedidosSatis.get(i+2)+", IDEmpregado:"+pedidosSatis.get(i+3)+", Estado:"+pedidosSatis.get(i+4)+", Quantidade:"+pedidosSatis.get(i+5)+"");
                    }
                    System.out.println("");
                    System.out.println("Pertende fechar algum pedido?, se sim introduza o id do pedido caso contrario introduza 0.");
                    idpedido = id_pedido.nextInt();
                    idPedidocheck = connect.checkPedido(idpedido,idempregado,idbar,"satisfeito");
                    if(idPedidocheck && idpedido > 0){
                        connect.updateEstadoPedido(idpedido,"fechado"); // muda o estado para fechado 
                        System.out.println("Pedido fechado com sucesso.");
                    }else{
                        System.out.println("Pedido inserido não existe na BD!");
                    }
                    break;
            }
        }while (pedidos != 0); 
       
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        new app_mesa();
        
       
    }  
}
