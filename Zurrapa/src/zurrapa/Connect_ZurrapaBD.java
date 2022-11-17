

package zurrapa;

import com.mysql.jdbc.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;


public class Connect_ZurrapaBD {

    private Connection connect;
    
    public Connect_ZurrapaBD(){
        try{
            Class.forName("com.mysql.jdbc.Driver"); // driver da ligação com work
            connect = (Connection) DriverManager.getConnection("jdbc:mysql://localhost:3306/zurrapa_bd?zeroDateTimeBehavior=convertToNull", "root", "");
            
        }catch(ClassNotFoundException e){
            System.out.println(e.getMessage());
        }catch(SQLException e) {
            System.out.println(e.getMessage());
        }
        
    }
    //verifica se existe ID empregado na tabela empregado_bar
    public boolean checkIdEmpregadoBar(int idemp, int idbar) throws SQLException{
        ResultSet resultTemp; 
        boolean resultFinal;
        Statement sta = connect.createStatement();
        resultTemp  = sta.executeQuery("SELECT IDEmpregado, IDBar FROM Empregado_bar WHERE IDEmpregado='"+idemp+"' AND IDBar='"+idbar+"'");
        resultFinal = resultTemp.next();
        return resultFinal; 
    }
    
    //Verifica se a mesa existe
    public boolean checkMesa(int idbar, int idmesa, int idemp) throws SQLException{
        ResultSet resultTemp; 
        boolean resultFinal;
        Statement sta = connect.createStatement();
        resultTemp  = sta.executeQuery("SELECT idMesa FROM Mesa WHERE (idMesa='"+idmesa+"' AND IDBar='"+idbar+"'AND Estado='"+idemp+"') OR (idMesa='"+idmesa+"' AND IDBar='"+idbar+"' AND Estado='')");
        resultFinal = resultTemp.next();
        return resultFinal; 
    }
    
    //verifica se existe mesa com estado null ou com o seu estado
    //lista mesas com estado a null ou com o seu estado
    public ArrayList<Integer> checkMesaLivre(int idbar, int idemp) throws SQLException{
        ResultSet resultTemp; 
        ArrayList<Integer> resultFinal = new ArrayList<>();
        Statement sta = connect.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        resultTemp  = sta.executeQuery("SELECT idMesa FROM Mesa WHERE "
                + "(IDBar='"+idbar+"'AND Estado='"+idemp+"') OR (IDBar='"+idbar+"' AND Estado='')");
        while(resultTemp.next()){
            resultFinal.add(resultTemp.getInt("idMesa"));
        }
        return resultFinal; 
    }
    //busca o id para lista os produtos existentes no bar
    public ArrayList<Integer> checkIDProduto(int idbar) throws SQLException{
        ResultSet resultTemp; 
        ArrayList<Integer> resultFinal = new ArrayList<>();
        Statement sta = connect.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        resultTemp  = sta.executeQuery("SELECT IDProduto FROM Produto_bar WHERE IDBar='"+idbar+"'");
        while(resultTemp.next()){
           resultFinal.add(resultTemp.getInt("IDProduto"));
        }
        
        return resultFinal; 
    }
    //lista produtos para o bar(atributos) usa a query de cima para saber o id do produto
    public ArrayList<String> checkProduto(int idproduto) throws SQLException{
        ResultSet resultTemp; 
        ArrayList<String> resultFinal = new ArrayList<>();
        Statement sta = connect.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        resultTemp  = sta.executeQuery("SELECT idProduto, Nome, Designacao, Preco FROM Produto WHERE idProduto='"+idproduto+"'");
        while(resultTemp.next()){
           resultFinal.add(resultTemp.getString("idProduto").toString());
           resultFinal.add(resultTemp.getString("Nome"));
           resultFinal.add(resultTemp.getString("Designacao"));
           resultFinal.add(resultTemp.getString("Preco").toString());
        }
        
        return resultFinal; 
    }
    //
    public int checkQuantidadeBar(int idproduto) throws SQLException{
        ResultSet resultTemp; 
        int resultFinal = 0;
        Statement sta = connect.createStatement();
        resultTemp  = sta.executeQuery("SELECT StockBar FROM Produto WHERE idProduto='"+idproduto+"'");
        while(resultTemp.next()){
            resultFinal = resultTemp.getInt("StockBar");
        }
        return resultFinal; 
    }
    
    public int checkQuantidadeArmazem(int idproduto) throws SQLException{
        ResultSet resultTemp; 
        int resultFinal = 0;
        Statement sta = connect.createStatement();
        resultTemp  = sta.executeQuery("SELECT StockArmazem FROM Produto WHERE idProduto='"+idproduto+"'");
        while(resultTemp.next()){
            resultFinal = resultTemp.getInt("StockArmazem");
        }
        return resultFinal; 
    }
    
    public ArrayList<Integer> checkMinhaMesa(int idbar, int idemp) throws SQLException{
        ResultSet resultTemp; 
        ArrayList<Integer> resultFinal = new ArrayList<>();
        Statement sta = connect.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        resultTemp  = sta.executeQuery("SELECT idMesa FROM Mesa WHERE IDBar='"+idbar+"' AND Estado='"+idemp+"'");
        while(resultTemp.next()){
            resultFinal.add(resultTemp.getInt("idMesa"));
        }
        return resultFinal; 
    }
    
    
    public void insertPedido(String data, int idmesa, int idempregado, int idbar, String estado, int quantidade, int idproduto) throws SQLException{
        Statement sta = connect.createStatement();
        sta.executeUpdate("INSERT INTO Pedido( Data, IDMesa, IDEmpregado, IDBar, Estado, Quantidade) VALUES('"+data+"', '"+idmesa+"', '"+idempregado+"', '"+idbar+"', '"+estado+"', '"+quantidade+"')");
        sta.executeUpdate("INSERT INTO Pedido_Produto (IDPedido, IDProduto) VALUES ((SELECT idPedido FROM Pedido ORDER BY idPedido DESC LIMIT 1), '"+idproduto+"')"); //vai inserir na tabela P_P mas busca o ultimo id do pedido ja adicionado
        sta.close();
    }
    
    //funão utilizada para quando o empregado faz o pedido e muda o estado para o seu id
    public void updateMesa(int idmesa, int idemp) throws SQLException{
        Statement sta = connect.createStatement();
        sta.executeUpdate("UPDATE Mesa SET Estado='"+idemp+"' WHERE idMesa='"+idmesa+"'");  
        sta.close();
    }
    // função usada para quando o empregado fecha mesa e mete o estado da mesa a null
    public void updateMesaFechada(int idmesa) throws SQLException{
        Statement sta = connect.createStatement();
        sta.executeUpdate("UPDATE Mesa SET Estado='' WHERE idMesa='"+idmesa+"'");  
        sta.close();
    }
    
    //QUERYS DA APLICAÇÃO EMPREGADO BALCÃO e tambem no MENU 2 da APP MESA
    
    public ArrayList<String> checkPedidos(int idbar, String estado) throws SQLException{
        int cont = 0;
        ResultSet resultTemp; 
        ArrayList<String> resultFinal = new ArrayList<>();
        Statement sta = connect.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
        resultTemp  = sta.executeQuery("SELECT idPedido, Data, IDMesa, IDEmpregado, Estado, Quantidade "
                + "FROM Pedido WHERE Estado='"+estado+"' AND IDBar='"+idbar+"'");
        while(resultTemp.next()){
            resultFinal.add(resultTemp.getString("idPedido").toString());
            resultFinal.add(resultTemp.getString("Data"));
            resultFinal.add(resultTemp.getString("IDMesa").toString());
            resultFinal.add(resultTemp.getString("IDEmpregado").toString());
            resultFinal.add(resultTemp.getString("Estado"));
            resultFinal.add(resultTemp.getString("Quantidade").toString());
        }
        
        return resultFinal; 
    }
    
    public boolean checkPedido(int idpedido, int idemp, int idbar, String estado) throws SQLException{
        ResultSet resultTemp; 
        boolean resultFinal = false;
        Statement sta = connect.createStatement();
        resultTemp  = sta.executeQuery("SELECT idPedido FROM Pedido "
                + "WHERE idPedido='"+idpedido+"' AND IDEmpregado='"+idemp +"' AND IDBar='"+idbar+"' AND Estado='"+estado+"'");
        resultFinal = resultTemp.next();
        return resultFinal; 
    }
    
    public void updateQuantProduto (int idpedido) throws SQLException{
        int quant = 0, novaquant = 0, idproduto = 0;
        ResultSet resultTemp; 
        Statement sta = connect.createStatement();
        resultTemp  = sta.executeQuery("SELECT Quantidade FROM Pedido WHERE idPedido='"+idpedido+"'");
        while(resultTemp.next()){
            quant = resultTemp.getInt("Quantidade"); // guarda a nova quantidade vinda da tabela pedido
        }
        
        resultTemp  = sta.executeQuery("SELECT IDProduto FROM Pedido_Produto WHERE IDPedido='"+idpedido+"'");
        while(resultTemp.next()){
            idproduto = resultTemp.getInt("IDProduto"); // guarda o id do produto para actualizar o valor no novo stock bar
        }
        
        resultTemp  = sta.executeQuery("SELECT StockBar FROM Produto WHERE idProduto='"+idproduto+"'");
        while(resultTemp.next()){
            novaquant = resultTemp.getInt("StockBar"); // busca a quantidade atual para atualizar no update
        }
        novaquant = novaquant - quant; // faz o valor do stock bar - a quantidade pedida
        sta.executeUpdate("UPDATE Produto SET StockBar='"+novaquant+"' WHERE idProduto='"+idproduto+"'");  
        sta.close();
    }
    
    public void updateEstadoPedido (int idpedido, String estado) throws SQLException{
        Statement sta = connect.createStatement();
        sta.executeUpdate("UPDATE Pedido SET Estado='"+estado+"' WHERE idPedido='"+idpedido+"'");  
        sta.close();
    }
    
        
    }
   
    
    
    

