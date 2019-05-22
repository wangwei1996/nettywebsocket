package link.bosswang.nettywebsocket;

public class Test {
    public static void main(String[] args){
        int index = 1;
        int newCapacity = index;
        newCapacity |= newCapacity >>>  1;
        newCapacity |= newCapacity >>>  2;
        newCapacity |= newCapacity >>>  4;
        newCapacity |= newCapacity >>>  8;
        newCapacity |= newCapacity >>> 16;
        newCapacity ++;
        System.out.println(newCapacity);
    }
}
