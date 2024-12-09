/**
 * Simple Drawing Application
 * 簡単なお絵かきソフト
 * ・フリーハンド，直線，四角，楕円の描画機能
 * ・四角と楕円は左下方向のみ
 * ・色などの変更機能は無し
 *
 * @author fukai
 */
import java.awt.*;
import java.awt.event.*;
import java.awt.Color.*;

public class DrawingApli00 extends Frame implements ActionListener {
  // ■ フィールド変数
  Button bt1, bt2, bt3, bt4, bt5, bt6, bt7, bt8, btBlack, btRed, btBlue, btYellow, btGreen; // フレームに配置するボタンの宣言
  Panel  pnl;                // ボタン配置用パネルの宣言
  MyCanvas mc;               // 別途作成した MyCanvas クラス型の変数の宣言
  static boolean red = false;
  static boolean blue = false;
  static boolean black = false;
  static boolean green = false;
  static boolean yellow = false;
  // ■ main メソッド（スタート地点）
  public static void main(String [] args) {
    DrawingApli00 da = new DrawingApli00();
  }

  // ■ コンストラクタ
  DrawingApli00() {
    super("Drawing Appli");
    this.setSize(900, 600);

    this.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 15));

    pnl = new Panel();       // Panel のオブジェクト（実体）を作成
    mc = new MyCanvas(this); // mc のオブジェクト（実体）を作成




    this.setLayout(new BorderLayout(10, 10)); // レイアウト方法の指定
    this.add(pnl, BorderLayout.EAST);         // 右側に パネルを配置

    this.add(mc,  BorderLayout.CENTER);       // 左側に mc （キャンバス）を配置
                                         // BorerLayout の場合，West と East の幅は
                                         // 部品の大きさで決まる，Center は West と East の残り幅
    pnl.setLayout(new GridLayout(9,1));  // ボタンを配置するため，９行１列のグリッドをパネル上にさらに作成
    bt1 = new Button("Free Hand"); bt1.addActionListener(this); pnl.add(bt1);// ボタンを順に配置
    bt2 = new Button("Line");      bt2.addActionListener(this); pnl.add(bt2);
    bt3 = new Button("Rectangle"); bt3.addActionListener(this); pnl.add(bt3);
    bt4 = new Button("Oval");      bt4.addActionListener(this); pnl.add(bt4);
    bt5 = new Button("FillRect");      bt5.addActionListener(this); pnl.add(bt5);
    bt6 = new Button("FillOval");      bt6.addActionListener(this); pnl.add(bt6);
    bt7 = new Button("Erase");      bt7.addActionListener(this); pnl.add(bt7);
    bt8 = new Button("Erase All");      bt8.addActionListener(this); pnl.add(bt8);

    Panel pnl_south = new Panel();
    this.add(pnl_south, BorderLayout.SOUTH);
    pnl_south.setLayout(new GridLayout(1, 5));
    btBlack = new Button("Black"); btBlack.addActionListener(this);  pnl_south.add(btBlack);
    btRed = new Button("Red");    btRed.addActionListener(this);   pnl_south.add(btRed);
    btBlue = new Button("Blue"); btBlue.addActionListener(this); pnl_south.add(btBlue);
    btYellow = new Button("Yellow"); btYellow.addActionListener(this); pnl_south.add(btYellow);
    btGreen = new Button("Green"); btGreen.addActionListener(this); pnl_south.add(btGreen);
    add(pnl_south, BorderLayout.SOUTH);           // パネルを SOUTH に配置

    this.setVisible(true); //可視化
  }

  // ■ メソッド
  // ActionListener を実装しているため、例え内容が空でも必ず記述しなければならない
  public void actionPerformed(ActionEvent e){ // フレーム上で生じたイベントを e で取得
    if (e.getSource() == bt1)      // もしイベントが bt1 で生じたなら
      mc.mode=1;                   // モードを１に
    else if (e.getSource() == bt2) // もしイベントが bt2 で生じたなら
      mc.mode=2;                   // モードを２に
    else if (e.getSource() == bt3) // もしイベントが bt3 で生じたなら
      mc.mode=3;                   // モードを３に
    else if (e.getSource() == bt4) // もしイベントが bt4 で生じたなら
      mc.mode=4;                   // モードを４に
    else if (e.getSource() == bt5) // もしイベントが bt5 で生じたなら
      mc.mode=5;
    else if (e.getSource() == bt6)
      mc.mode=6;
    else if(e.getSource() == bt7)
      mc.mode=7;
    else if (e.getSource() == bt8)
      mc.mode=8;
    else if (e.getSource() == btBlack){
      blue = false; black = true; red = false; yellow = false; green = false;
    }
    else if (e.getSource() == btRed){
      blue = false; black = false; red = true; yellow = false; green = false;
    }
    else if (e.getSource() == btBlue){
      blue = true; black = false; red = false; yellow = false; green = false;
    }
    else if (e.getSource() == btYellow){
      yellow = true; blue = false; black = false; red = false;  green = false;
    }
    else if (e.getSource() == btGreen){
      green = true; blue = false; black = false; red = false; yellow = false;
    }
  }
}



/**
 * Extended Canvas class for DrawingApli
 * [各モードにおける処理内容]
 * 1: free hand
 *      pressed -> set x, y,  dragged  -> drawline & call repaint()
 * 2: draw line
 *      pressed -> set x, y,  released -> drawline & call repaint()
 * 3: rect
 *      pressed -> set x, y,  released -> calc w, h & call repaint()
 * 4: circle
 *      pressed -> set x, y,  released -> calc w, h & call repaint()
 *
 * @author fukai
 */
class MyCanvas extends Canvas implements MouseListener, MouseMotionListener {
  // ■ フィールド変数
  int x, y;   // mouse pointer position
  int px, py, r, s; // preliminary position 予備
  int ow, oh; // width and height of the object
  int mode;   // drawing mode associated as below
  Image img = null;   // 仮の画用紙
  Graphics gc = null; // 仮の画用紙用のペン
  Dimension d; // キャンバスの大きさ取得用

  // ■ コンストラクタ
  MyCanvas(DrawingApli00 obj){
    mode=0;                       // initial value
    this.setSize(500,400);        // キャンバスのサイズを指定
    addMouseListener(this);       // マウスのボタンクリックなどを監視するよう指定
    addMouseMotionListener(this); // マウスの動きを監視するよう指定
  }

  // ■ メソッド（オーバーライド）
  // フレームに何らかの更新が行われた時の処理
  public void update(Graphics g) {
    paint(g); // 下記の paint を呼び出す
  }

  // ■ メソッド（オーバーライド）
  public void paint(Graphics g) {
    d = getSize();   // キャンバスのサイズを取得
    if (img == null) // もし仮の画用紙の実体がまだ存在しなければ
      img = createImage(d.width, d.height); // 作成
    if (gc == null)  // もし仮の画用紙用のペン (GC) がまだ存在しなければ
      gc = img.getGraphics(); // 作成

    if(DrawingApli00.black){
      gc.setColor(Color.black);
    }
    else if(DrawingApli00.red){
      gc.setColor(Color.red);
    }
    else if(DrawingApli00.blue){
      gc.setColor(Color.blue);
    }
    else if(DrawingApli00.green){
      gc.setColor(Color.green);
    }
    else if(DrawingApli00.yellow){
      gc.setColor(Color.yellow);
    }
    else{
      gc.setColor(Color.black);
    }

    switch (mode){
    case 1: // モードが１の場合
      gc.drawLine(px, py, x, y); // 仮の画用紙に描画
      break;
    case 2: // モードが２の場合
      gc.drawLine(px, py, x, y); // 仮の画用紙に描画
      break;
    case 3: // モードが３の場合
      gc.drawRect(r, s, ow, oh); // 仮の画用紙に描画
      break;
    case 4: // モードが４の場合
      gc.drawOval(r, s, ow, oh); // 仮の画用紙に描画
      break;
    case 5: // モードが５の場合
      gc.fillRect(r, s, ow, oh);
      break;
    case 6:
      gc.fillOval(r, s, ow, oh);

      break;
    case 7:
      gc.clearRect(px, py, 20, 20);
      break;
    case 8:
      gc.clearRect(0, 0, d.width, d.height);
      repaint();
      break;
    }
    g.drawImage(img, 0, 0, this); // 仮の画用紙の内容を MyCanvas に描画
  }

  // ■ メソッド
  // 下記のマウス関連のメソッドは，MouseListener をインターフェースとして実装しているため
  // 例え使わなくても必ず実装しなければならない
  public void mouseClicked(MouseEvent e){}// 今回は使わないが、無いとコンパイルエラー
  public void mouseEntered(MouseEvent e){}// 今回は使わないが、無いとコンパイルエラー
  public void mouseExited(MouseEvent e){} // 今回は使わないが、無いとコンパイルエラー
  public void mousePressed(MouseEvent e){ // マウスボタンが押された時の処理
    switch (mode){
    case 1: // mode が１の場合，次の内容を実行する
      x = e.getX();
      y = e.getY();
      break;
    case 2: // mode が２もしくは
    case 3: // ３もしくは
    case 4: // ４の場合，次の内容を実行する
    case 5:
    case 6:
    case 7:
    case 8:
      px = e.getX();
      py = e.getY();
    }
  }
  public void mouseReleased(MouseEvent e){ // マウスボタンが離された時の処理
    switch (mode){
    case 2: // mode が２もしくは
    case 3: // ３もしくは
    case 4: // ４の場合，次の内容を実行する
    case 5:
    case 6:
    case 7:
    case 8:
      x = e.getX();
      y = e.getY();
      if(x>px && y>py){
        ow = x-px;
        oh = y-py;
        r = px;
        s = py;
      }
      else if(x>px && y<py){
        ow = x-px;
        oh = py-y;
        r = px;
        s = y;
      }
      else if(x<px && y>py){
        ow = px-x;
        oh = y-py;
        r = x;
        s = py;
      }
      else{
        ow = px-x;
        oh = py-y;
        r = x;
        s = y;
      }
      repaint(); // 再描画
    }
  }

  // ■ メソッド
  // 下記のマウス関連のメソッドは，MouseMotionListener をインターフェースとして実装しているため
  // 例え使わなくても必ず実装しなければならない
  public void mouseDragged(MouseEvent e){ // マウスがドラッグされた時の処理
    switch (mode){
    case 1: // mode が１の場合，次の内容を実行する
    case 7:
      px = x;
      py = y;
      x = e.getX();
      y = e.getY();
      repaint(); // 再描画
    }
  }
  public void mouseMoved(MouseEvent e){} // 今回は使わないが、無いとコンパイルエラー
}
