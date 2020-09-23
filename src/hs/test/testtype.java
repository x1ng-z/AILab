package hs.test;

/**
 * @author zzx
 * @version 1.0
 * @date 2020/9/17 15:16
 */
public class testtype {

    public <T extends Color> T getItem(T ietm){
        return ietm;
    }
    public static  <T extends Color> T staticgetItem(T ietm){
        return ietm;
    }


    public static void main(String[] args) {


        staticgetItem(new Color() {
            @Override
            public void drow() {
                System.out.println(getClass());
            }
        }).drow();

        new testtype().getItem(new Color() {
            @Override
            public void drow() {
                System.out.println(getClass());
            }
        }).drow();

    }
}

interface Color{
    void drow();
}

class Hold<T>{
    T item;
    T getItem(){
        return item;
    }
}

class sonHold<T extends Color> extends Hold<T>{
    public void drow(){
        item.drow();
    }

}

class soonsonHold<T extends Color> extends sonHold<T>{
    @Override
    public void drow(){
    }

}
