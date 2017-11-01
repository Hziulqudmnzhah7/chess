import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
public class chess extends JFrame{// implements ActionListener{
   JButton[][]	array=new JButton[8][8];
   private static final Color STARTCOLOR=Color.YELLOW;
   private static final Color MOVEABLECOLOR=Color.ORANGE;
   private static final Color TAKEABLECOLOR=Color.BLUE;
   private static final Color PASSABLECOLOR=Color.GREEN;
   private static final Color CASTLINGCOLOR=Color.magenta;
   private static final String FILENAME="board.txt";
   private static final Map<Byte,String> map = new HashMap<Byte, String>();
   static {
      map.put((byte)1,"white Pawn");
      map.put((byte)2,"white Rook");
      map.put((byte)3,"white Knight");
      map.put((byte)4,"white Bishop");
      map.put((byte)5,"white King");
      map.put((byte)6,"white Queen");
      map.put((byte)7,"black Pawn");
      map.put((byte)8,"black Rook");
      map.put((byte)9,"black Knight");
      map.put((byte)10,"black Bishop");
      map.put((byte)11,"black King");
      map.put((byte)12,"black Queen"); 
   }
   private boolean wKingSide=true, wQueenSide=true, bKingSide=true, bQueenSide=true;
   private byte[][] taken = {//piece map#,times taken //for pawn upgrade
      {1,0}, {2,0}, {3,0}, {4,0}, {5,0}, {6,0}, {7,0}, {8,0}, {9,0}, {10,0}, {11,0}, {12,0},
   };
   private byte[][] board = {//starting board
      {2,3,4,5,6,4,3,2}, //1
      {1,1,1,1,1,1,1,1},//2
      {0,0,0,0,0,0,0,0,},//3
      {0,0,0,0,0,0,0,0,},//4
      {0,0,0,0,0,0,0,0,},//5
      {0,0,0,0,0,0,0,0,},//6
      {7,7,7,7,7,7,7,7},//7
      {8,9,10,12,11,10,9,8}//8
   };
   private char turn='b';//w=whites turn; b=blacks turn //not implemented yet
   private void changeTurn(){//not implemented yet
      if (turn=='w')
         turn='b';
      else
         turn='w';
      for (int i=0;i<8;i++){
         for (int j=0;j<8;j++){
            //if (board[i][j]!=0) 
         }
      }
   }
   private void refresh(){
      for (int i=0;i<8;i++){
         for (int j=0;j<8;j++){
            if	((i%2==0&&j%2==0)||(i%2==1&&j%2==1)) array[i][j].setBackground(Color.WHITE); //sets board color
            else array[i][j].setBackground(Color.RED); //sets board color
            if (board[i][j]!=0) array[i][j].setIcon(new ImageIcon(""+map.get(board[i][j]).split(" ")[0]+map.get(board[i][j]).split(" ")[1]+".png"));
            else array[i][j].setIcon(null);
            array[i][j].putClientProperty("selected",null);
         }
      }
   }
   private void movePawn(int i, int j){
      byte colored;
      if (map.get(board[i][j]).split(" ")[0].equals("white")) 
         colored=1;//white
      else 
         colored=-1; //black
      if (i==7||i==0){//prompt for upgrade
         ArrayList<Object> possibilities = new ArrayList();
         for (int p=0;p<12;p++){
            if (taken[p][1]!=0&&map.get(board[i][j]).split(" ")[0].equals(map.get(taken[p][0]).split(" ")[0]))
               possibilities.add(map.get(taken[p][0]));
         }
         String s=(String)JOptionPane.showInputDialog(new JFrame(),"choose piece to replace your pawn","upgrade",JOptionPane.PLAIN_MESSAGE, null, possibilities.toArray(),null);
         for (Map.Entry<Byte, String> entry : map.entrySet()) {
            if (entry.getValue().equals(s))
               board[i][j]=(byte)entry.getKey();
         }
         refresh();
         return;
      }//end upgrade
      if ((i==4&&colored==1)||(i==3&&colored==-1)){//passed pawn
         if (j!=0&&board[i][j-1]!=0&&board[i+colored][j-1]==0)
            array[i+colored][j-1].setBackground(PASSABLECOLOR);
         if (j!=7&&board[i][j+1]!=0&&board[i+colored][j+1]==0)
            array[i+colored][j+1].setBackground(PASSABLECOLOR);
      }
      if (j!=7&&board[i+colored][j+1]!=0)//diagonal attack right
         moveable(i,j,colored,1);
      if (j!=0&&board[i+colored][j-1]!=0)//diagonal attack left
         moveable(i,j,colored,-1);
      if (board[i+colored][j]==0)//move once
         array[i+colored][j].setBackground(MOVEABLECOLOR);
      if (((i==1&&colored==1&&board[i+(colored*2)][j]==0)||(i==6&&colored==-1&&board[i+(colored*2)][j]==0))&&board[i+colored][j]==0)//move twice
         array[i+(colored*2)][j].setBackground(MOVEABLECOLOR);
   }
   //private A
   private boolean moveable(int i, int j, int p, int l){
      if (i+p<8&&i+p>-1&&j+l<8&&j+l>-1&&board[i+p][j+l]!=0){// in bounds and !0
         if (!map.get(board[i+p][j+l]).split(" ")[0].equals(map.get(board[i][j]).split(" ")[0]))
            array[i+p][j+l].setBackground(TAKEABLECOLOR);
         return true;
      } else if (i+p<8&&i+p>-1&&j+l<8&&j+l>-1) //in bounds and 0
         array[i+p][j+l].setBackground(MOVEABLECOLOR);
      return false;
   }
   private void Move(int i, int j){//void move(int, int)<---already taken in Component
      if (array[i][j].getBackground()==MOVEABLECOLOR||array[i][j].getBackground()==TAKEABLECOLOR||array[i][j].getBackground()==PASSABLECOLOR||array[i][j].getBackground()==CASTLINGCOLOR){//actually moves and takes pieces
         Point x=new Point();
         for (int	a=0;a<8;a++)
            for (int b=0;b<8;b++)//board[i][j] = new location
               if (array[a][b].getBackground()==STARTCOLOR)//get start location
                  x=new Point(a,b);//new location as point
         if (array[i][j].getBackground()==PASSABLECOLOR){
            if (map.get(board[x.x][x.y]).split(" ")[0].equals("white")){
               taken[board[i-1][j]-1][1]++;
               board[i-1][j]=0;
            }
            else if (map.get(board[x.x][x.y]).split(" ")[0].equals("black")){
               taken[board[i+1][j]-1][1]++;
               board[i+1][j]=0;
            }
         }
         if (array[i][j].getBackground()==CASTLINGCOLOR){
            byte h=0;
            if (map.get(board[i][j]).split(" ")[0].equals("white"))
               h=1;
            else if (map.get(board[i][j]).split(" ")[0].equals("black"))
               h=-1;
            if (j==0&&h==1||j==7&&h==-1){//king side
               if (h==1) wKingSide=false;
               else bKingSide=false;
               board[i][j+h]=board[x.x][x.y];
               board[i][j+(2*h)]=board[i][j];
               board[i][j]=0;
               board[x.x][x.y]=0;
            }
            if (j==0&&h==-1||j==7&&h==1){//queen side
               if (h==1) wQueenSide=false;
               else bQueenSide=false;
               board[i][j+(-2*h)]=board[x.x][x.y];
               board[i][j+(-3*h)]=board[i][j];
               board[i][j]=0;
               board[x.x][x.y]=0;
            }
         }//end castling
         if (array[i][j].getBackground()==TAKEABLECOLOR)//add removed peice to the takable array
            taken[board[i][j]-1][1]++;
         if (board[x.x][x.y]==5){//when king moves prevent castling
            wKingSide=false;
            wQueenSide=false;   
         }
         if (board[x.x][x.y]==2&&i==0&&j==0)//when rook moves prevent castling on that side
            wKingSide=false;
         if (board[x.x][x.y]==2&&i==0&&j==7)//when rook moves prevent castling on that side
            wQueenSide=false;
         if (board[x.x][x.y]==8&&i==7&&j==0)//when rook moves prevent castling on that side
            bQueenSide=false;
         if (board[x.x][x.y]==8&&i==7&&j==7)//when rook moves prevent castling on that side
            bKingSide=false;
         if (board[x.x][x.y]==11){//when king moves prevent castling
            bKingSide=false;
            bQueenSide=false;   
         }
         board[i][j]=board[x.x][x.y];
         board[x.x][x.y]=0;
         refresh();
         return;
      }//end actually move
      else if (array[i][j].getClientProperty("selected")==null){
         refresh();
         array[i][j].setBackground(STARTCOLOR);
         array[i][j].putClientProperty("selected",true);
      }
      else{
         refresh();
         return;
      }
      if (board[i][j]!=0&&map.get(board[i][j]).split(" ")[1].equals("Pawn"))
         movePawn(i,j);
      else if (board[i][j]!=0&&(map.get(board[i][j]).split(" ")[1].equals("Rook")||map.get(board[i][j]).split(" ")[1].equals("Bishop")||map.get(board[i][j]).split(" ")[1].equals("Queen"))){
         if (map.get(board[i][j]).split(" ")[1].equals("Rook")||map.get(board[i][j]).split(" ")[1].equals("Queen")){
            for (int p=1,h=0b10000;p<9;p++){
               if ((h & 1<<3)==0&&moveable(i,j,p,0))
                  h |= 1<<3;
               if ((h & 1<<2)==0&&moveable(i,j,-p,0))
                  h |= 1<<2;
               if ((h & 1<<1)==0&&moveable(i,j,0,p))
                  h |= 1<<1;
               if ((h & 1<<0)==0&&moveable(i,j,0,-p))
                  h |= 1<<0;
            }   
         }//end rook
         if (map.get(board[i][j]).split(" ")[1].equals("Bishop")||map.get(board[i][j]).split(" ")[1].equals("Queen")){
            for (int p=1,h=0b10000;p<9;p++){
               if ((h & 1<<3)==0&&moveable(i,j,p,p))
                  h |= 1<<3;
               if ((h & 1<<2)==0&&moveable(i,j,-p,p))
                  h |= 1<<2;
               if ((h & 1<<1)==0&&moveable(i,j,p,-p))
                  h |= 1<<1;
               if ((h & 1<<0)==0&&moveable(i,j,-p,-p))
                  h |= 1<<0;
            }   
         }//end bishop
      }//end bishop, rook and queen
      else if (board[i][j]!=0&&map.get(board[i][j]).split(" ")[1].equals("Knight")){
         moveable(i,j,2,1); moveable(i,j,2,-1); moveable(i,j,-2,1); moveable(i,j,-2,-1);
         moveable(i,j,1,2); moveable(i,j,-1,2); moveable(i,j,1,-2); moveable(i,j,-1,-2);
      }//end knight
      else if (board[i][j]!=0&&map.get(board[i][j]).split(" ")[1].equals("King")){
         moveable(i,j,1,0); moveable(i,j,-1,0); moveable(i,j,0,1); moveable(i,j,0,-1); 
         moveable(i,j,1,1); moveable(i,j,-1,1); moveable(i,j,1,-1); moveable(i,j,-1,-1);
         if (wKingSide&&board[0][0]==2&&board[0][1]==0&&board[0][2]==0&&board[0][3]==5)// white king side
            array[0][0].setBackground(CASTLINGCOLOR);
         if (wQueenSide&&board[0][7]==2&&board[0][6]==0&&board[0][5]==0&&board[0][4]==0&&board[0][3]==5) //white queen side
            array[0][7].setBackground(CASTLINGCOLOR);
         if (bKingSide&&board[7][7]==8&&board[7][6]==0&&board[7][5]==0&&board[7][4]==11)// black king side
            array[7][7].setBackground(CASTLINGCOLOR);
         if (bQueenSide&&board[7][0]==8&&board[7][1]==0&&board[7][2]==0&&board[7][3]==0&&board[7][4]==11)// black queen side
            array[7][0].setBackground(CASTLINGCOLOR);
         //
      }//end king
   }//end move
   public chess(String title){ //panel contructor
      super(title);
      setLayout(new GridLayout(8,8));
      for (int	i=0;i<8;i++){
         for (int	j=0;j<8;j++){
            array[i][j]=new JButton();
            array[i][j].setOpaque(true); // make board nicer to look at
            array[i][j].setBorderPainted(false); // ^
            final int iIndex=i,jIndex=j;
            array[i][j].addActionListener(e -> {// on click button actions go here
               Move(iIndex,jIndex);
            });//end lambda
            add(array[i][j]);
         }//j
      }//i
      refresh();
      //frame and menu below
      JMenuBar bar = new JMenuBar();
      JMenu menu = new JMenu("Menu");
      JMenuItem save = new JMenuItem("save game"); 
      JMenuItem open = new JMenuItem("open game");
      save.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK)); 
      open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
      save.addActionListener(e -> {
         try {
            write(board,FILENAME);
         } catch (IOException ex){
            System.out.println("something happen"); //<--windows 10 throwback
         }
      });//end lambda
      open.addActionListener(e -> {
         try {
            read(FILENAME);
         } catch (IOException ex){
            System.out.println("something happen"); //<--windows 10 throwback
         }
      });//end lambda
      menu.add(save);
      menu.add(open);
      bar.add(menu);
      setJMenuBar(bar);
   }
   public void write(byte[][] array, String filename) throws IOException{//writes to file
      System.setOut(new PrintStream(new FileOutputStream(filename)));
      for(byte[] x : array){
         for(byte y: x)
            System.out.print(y+" ");
        System.out.println("");    
      }
      System.out.println(wKingSide+" "+wQueenSide+" "+bKingSide+" "+bQueenSide);
      for(byte[] x: taken)
         System.out.print(x[1]+" ");
   }
   public void read(String fileName)throws IOException{//reads from file
      Scanner input = new Scanner(new FileReader(fileName));	
      for (byte i=0;i<board.length&&input.hasNextLine();i++){
         String[] temp=input.nextLine().split(" ");
         for (byte j=0;j<board[i].length;j++){
            board[i][j]=Byte.parseByte(temp[j]);
         }
      }
      String[] temp = input.nextLine().split(" ");
      wKingSide=Boolean.parseBoolean(temp[0]);
      wQueenSide=Boolean.parseBoolean(temp[1]);
      bKingSide=Boolean.parseBoolean(temp[2]);
      bQueenSide=Boolean.parseBoolean(temp[3]);
      temp = input.nextLine().split(" ");
      for(int i=0;i<taken.length;i++)
         taken[i][1]=Byte.parseByte(temp[i]);
      input.close();
      refresh();//refreshs pieces
   }
   public static void main(String args[]) throws IOException{
      chess c = new chess("chess");
      c.setVisible(true);
      c.setSize(800,800);
   	//frame.setLocation(200,200); //no no at home
      c.setResizable(false);
      c.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   }
}//class
