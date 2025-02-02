package LabsForJava;

import java.util.*;

import static java.lang.Integer.MIN_VALUE;

public class Lab1 {
    public static void main(String[] args) {
        //1
        /* Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int counter = 0;
        while (n != 1) {
            if (n % 2 == 0) {
                n /= 2;
                counter++;
            }
            else {
                n = n*3 + 1;
                counter++;
            }
        }
        System.out.println(counter);*/

        //2
        /*Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        List<Integer> listValues = new ArrayList<Integer>();
        while(n != 0){
            int value = scanner.nextInt();
            listValues.add(value);
            n-=1;
        }
        int total = 0;
        for(int i = 0;i < listValues.size();i++){
            if (i%2 ==0){
                total = total + listValues.get(i);
            }
            else{
                total = total - listValues.get(i);
            }
        }
        System.out.println(total);*/

        //3
        /*Scanner scanner = new Scanner(System.in);
        int x = scanner.nextInt();
        int y = scanner.nextInt();
        String word = scanner.next();
        int step = scanner.nextInt();
        int counter = 0;
        while(true){
            word = scanner.next();
            if (word.equals("стоп"))
            {
                break;
            }
            step = scanner.nextInt();
            counter++;

        }
        System.out.println(counter);*/

        //4
        /*Scanner scanner = new Scanner(System.in);
        int Roads = scanner.nextInt();
        int counter = 0;
        HashMap<Integer,Integer> map = new HashMap<Integer, Integer>();
        while(Roads!=0){
            int Tunnels = scanner.nextInt();
            counter++;
            List<Integer> currentTunnel = new ArrayList<>();
            while (Tunnels!=0){
                int height = scanner.nextInt();
                currentTunnel.add(height);
                Tunnels-=1;
            }
            Collections.sort(currentTunnel);
            map.put(counter,currentTunnel.get(0));
            Roads-=1;
        }
        int max = MIN_VALUE;
        int key = 0;
        for (Map.Entry currentMap: map.entrySet()){

            if (max < (int) currentMap.getValue()){
                max = (int) currentMap.getValue();
                key = (int) currentMap.getKey();
            }
        }
        System.out.println(key+" "+map.get(key));*/


        //5
        /*Scanner scanner = new Scanner(System.in);
        int value = scanner.nextInt();
        int sumDigits = (value%10)+(value/10%10)+(value/100);
        int przvDigits = (value%10)*(value/10%10)*(value/100);
        if (sumDigits%2==0 && przvDigits%2==0){
            System.out.println("Число: "+value+" является дважды четным");
        }
        else{
            System.out.println("Число: "+value+" не является дважды четным");
        }*/
    }
}
