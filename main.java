import java.util.concurrent.locks.*;
import java.util.concurrent.ThreadLocalRandom;
abstract class Element implements Runnable
{
    protected String tip;
    
    protected int x,y;

    public void poz()
    {
        System.out.println(tip+" ("+x+","+y+")");
    }

    public Molecula react(Element a)
    {
        if(this.tip.equals("O") && a.tip.equals("H"))
        {
            return new Molecula("OH");
        }
        if(this.tip.equals("O") && a.tip.equals("Na"))
        {
            return new Molecula("NaO");
        }
        if(this.tip.equals("Cl") && a.tip.equals("H"))
        {
            return new Molecula("HCl");
        }
        if(this.tip.equals("Cl") && a.tip.equals("Na"))
        {
            return new Molecula("NaCl");
        }
        if(this.tip.equals("O") && a.tip.equals("C"))
        {
            return new Molecula("CO");
        }
        if(this.tip.equals("Pl") && a.tip.equals("U"))
        {
            return new Molecula("UPl");
        }
        if(this.tip.equals("Hg") && a.tip.equals("Cl"))
        {
            return new Molecula("HgCl");
        }
        if(this.tip.equals("O") && a.tip.equals("Au"))
        {
            return new Molecula("AuO");
        }
        if(this.tip.equals("Ag") && a.tip.equals("Au"))
        {
            return new Molecula("AgAu");
        }
        return null;
    }
    
    public Element(String t)
    {
        this.tip=t;
    }
    public String toString()
    {
        return tip;
    }

    public void run()
    {
        int px=0,py=0;
        while(true)
        {
        
        int p=ThreadLocalRandom.current().nextInt(0, 8);
        switch (p) {
            case 0:
                px=-1;
                py=1;
                break;
            case 1:
                px=0;
                py=1;
                break;
            case 2:
                px=1;
                py=1;
                break;
            case 3:
                px=1;
                py=0;
                break;
            case 4:
                px=1;
                py=-1;
                break;
            case 5:
                px=0;
                py=-1;
                break;
            case 6:
                px=-1;
                py=-1;
                break;
            case 7:
                px=-1;
                py=0;
                break;
        }
        if(x==0 && y==0)
        {
            px=1;
            py=1;
        }else
        if(x==0 && y==9)
        {
            px=1;
            py=-1;
        }else
        if(x==9 && y==0)
        {
            px=-1;
            py=1;
        }else
        if(x==9 && y==9)
        {
            px=-1;
            py=-1;
        }
        else if(x+px>9 || x+px<0 || y+py>9 || y+py<0)
        {
            px=-px;
            py=-py;
        }
        if(Map.locks[x][y].tryLock())
        {
            if(Map.locks[x+px][y+py].tryLock())
            {
                if(Map.area[x+px][y+py]==null)
                {
                    Map.area[x+px][y+py]=this;
                    Map.area[x][y]=null;
                    Map.locks[x][y].unlock();
                    y+=py;
                    x+=px;
                    Map.locks[x][y].unlock();
                }
                else
                {
                    Molecula m = Map.area[x+px][y+py].react(Map.area[x][y]);
                    if(m!=null)
                    {
                        Map.area[x+px][y+py]=m;
                        m.x=x+px;
                        m.y=y+py;
                        Map.area[x][y]=null;
                        System.out.println(m);
                    }
                    else
                    {
                        
                    }
                    Map.locks[x+px][y+py].unlock();
                    Map.locks[x][y].unlock();  
                }
            }
        }
        poz();
        }
    }
}

class Molecula extends Element 
{
    public Molecula(String tip)
    {
        super(tip);
    }
}

class Atom extends Element
{
    
    public Atom(String t)
    {
        super(t);
    }
}

class Map
{
    public static Element[][] area=new Element[10][10]; 
    public static ReentrantLock[][] locks=new ReentrantLock[10][10];
    
    public void add(Atom a)
    {
        a.x=(int)Math.floor(Math.random()*10);
        a.y=(int)Math.floor(Math.random()*10);
        while(area[a.x][a.y]!=null){
            a.x=(int)Math.floor(Math.random()*10);
            a.y=(int)Math.floor(Math.random()*10);
        }
        area[a.x][a.y]=a;
    }
}

class Main
{
    public static void main(String[] args)
    {
        Thread[] t=new Thread[9];
        Atom[] a=new Atom[9];
        Map map=new Map();
        a[0]=new Atom("O");
        a[1]=new Atom("H");
        a[2]=new Atom("Cl");
        a[3]=new Atom("Na");
        a[4]=new Atom("C");
        a[5]=new Atom("Au");
        a[6]=new Atom("Ag");
        a[7]=new Atom("Pl");
        a[8]=new Atom("U");

        for(int i=0;i<10;i++)
            for(int j=0;j<10;j++)
            {
                Map.locks[i][j]=new ReentrantLock();
            }

        for(int i=0;i<9;i++)
        {
            map.add(a[i]);
            t[i] = new Thread(a[i]);    
            t[i].start();
        }
    }
}