

CREATE TABLE `Bar` TABLE IF NOT EXISTS(
  `idBar` int(11) NOT NULL AUTO_INCREMENT,
  `Nome` varchar(45) NOT NULL,
  `Local` varchar(45) NOT NULL,
  `IDEmpregado` int(11) NOT NULL,
  PRIMARY KEY (`idBar`),
  KEY `fk_idEmpregado2_idx` (`IDEmpregado`),
  CONSTRAINT `fk_idEmpregado2` FOREIGN KEY (`IDEmpregado`) REFERENCES `Empregado` (`idEmpregado`) ON DELETE CASCADE ON UPDATE CASCADE
) 

CREATE TABLE `Empregado` TABLE IF NOT EXISTS(
  `idEmpregado` int(11) NOT NULL AUTO_INCREMENT,
  `Nome` varchar(45) NOT NULL,
  `Idade` int(11) NOT NULL,
  `Sexo` varchar(1) NOT NULL,
  `Tipo` varchar(45) NOT NULL,
  PRIMARY KEY (`idEmpregado`)
) 

CREATE TABLE `Empregado_Bar` TABLE IF NOT EXISTS(
  `IDEmpregado` int(11) NOT NULL,
  `IDBar` int(11) NOT NULL,
  KEY `fk_idEmpregado3_idx` (`IDEmpregado`),
  KEY `fk_idBar5_idx` (`IDBar`),
  CONSTRAINT `fk_idBar5` FOREIGN KEY (`IDBar`) REFERENCES `Bar` (`idBar`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_idEmpregado3` FOREIGN KEY (`IDEmpregado`) REFERENCES `Empregado` (`idEmpregado`) ON DELETE CASCADE ON UPDATE CASCADE
) 



CREATE TABLE `Mesa` TABLE IF NOT EXISTS(
  `idMesa` int(11) NOT NULL AUTO_INCREMENT,
  `IDBar` int(11) NOT NULL,
  `Estado` varchar(45) NOT NULL,
  PRIMARY KEY (`idMesa`),
  KEY `idBar_idx` (`IDBar`),
  CONSTRAINT `fk_idBar2` FOREIGN KEY (`IDBar`) REFERENCES `Bar` (`idBar`) ON DELETE CASCADE ON UPDATE CASCADE
)


CREATE TABLE `Pedido` TABLE IF NOT EXISTS(
  `idPedido` int(11) NOT NULL AUTO_INCREMENT,
  `Data` varchar(50) NOT NULL,
  `IDMesa` int(11) NOT NULL,
  `IDEmpregado` int(11) NOT NULL,
  `IDBar` int(11) NOT NULL,
  `Estado` varchar(45) DEFAULT NULL,
  `Quantidade` int(11) NOT NULL,
  PRIMARY KEY (`idPedido`),
  KEY `fk_idMesa1_idx` (`IDMesa`),
  KEY `fk_idEmpregado1_idx` (`IDEmpregado`),
  KEY `fk_idBar3_idx` (`IDBar`),
  CONSTRAINT `fk_idBar3` FOREIGN KEY (`IDBar`) REFERENCES `Bar` (`idBar`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_idEmpregado1` FOREIGN KEY (`IDEmpregado`) REFERENCES `Empregado` (`idEmpregado`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_idMesa1` FOREIGN KEY (`IDMesa`) REFERENCES `Mesa` (`idMesa`) ON DELETE CASCADE ON UPDATE CASCADE
) 


CREATE TABLE `Pedido_Produto` TABLE IF NOT EXISTS(
  `IDPedido` int(11) NOT NULL,
  `IDProduto` int(11) NOT NULL,
  KEY `fk_idProduto1_idx` (`IDProduto`),
  KEY `fk_idPedido3_idx` (`IDPedido`),
  CONSTRAINT `fk_idPedido3` FOREIGN KEY (`IDPedido`) REFERENCES `Pedido` (`idPedido`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_idProduto1` FOREIGN KEY (`IDProduto`) REFERENCES `Produto` (`idProduto`) ON DELETE CASCADE ON UPDATE CASCADE
) 


CREATE TABLE `Produto` TABLE IF NOT EXISTS(
  `idProduto` int(11) NOT NULL AUTO_INCREMENT,
  `Nome` varchar(45) NOT NULL,
  `Designacao` varchar(45) NOT NULL,
  `Preco` decimal(10,0) NOT NULL,
  `StockArmazem` int(11) NOT NULL,
  `StockBar` int(11) NOT NULL,
  PRIMARY KEY (`idProduto`)
) 


CREATE TABLE `Produto_Bar` TABLE IF NOT EXISTS(
  `IDProduto` int(11) NOT NULL,
  `IDBar` int(11) NOT NULL,
  KEY `fk_idBar4_idx` (`IDBar`),
  KEY `fk_idProduto2_idx` (`IDProduto`),
  CONSTRAINT `fk_idBar4` FOREIGN KEY (`IDBar`) REFERENCES `Bar` (`idBar`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_idProduto2` FOREIGN KEY (`IDProduto`) REFERENCES `Produto` (`idProduto`) ON DELETE CASCADE ON UPDATE CASCADE
) 
