import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class SnakeLadderProblem {
    static int countA = 0;
    static int countB =0;
    static List<Integer> userAList = new ArrayList<>();
    static List<Integer> userBList = new ArrayList<>();
    public static int[][] inputArray()throws IOException {
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        System.out.println("Enter datapoint rows");
        int n = Integer.parseInt(br.readLine());
        int arr[][] = new int[n][n];
        System.out.println("enter pitstops input takes matrix of" + n + "*" + n + "no. should be separated by a space");
        String numbers = br.readLine();
        String[] split = numbers.split(" ");
        int j = 0, k=0;
        for(int i =0; i<split.length; i++){
            if(k>=n){
                k=0;
                j++;
            }
            arr[j][k] = Integer.parseInt(split[i]);
            k++;
        }

       /* for(int i =0; i<n;i++){
            for(int l =0; l< n; l++){
                System.out.print(arr[i][l] + " ");
            }
            System.out.println();
        }*/
       return arr;
    }
    public static void main(String[] args)throws IOException {
        int[][] array = inputArray();
        AtomicBoolean playerA= new AtomicBoolean(true);
//        snackLadder(10, 10, 3, true);
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        final Object lock = new Object();
        executorService.execute(() -> {
//            Thread.currentThread().setName("Test1");
            while (countA != 100 && countB!= 100) {
                if (playerA.get()) {
                    try {
                        drawValue('A', array);
                        playerA.set(false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    synchronized (lock) {
                        lock.notify();
                    }
                } else if (!playerA.get()) {
                    try {
                        drawValue('B', array);
                        playerA.set(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    synchronized (lock) {
                        lock.notify();
                    }
                } else {
                    synchronized (lock) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            printArray();

        });
        executorService.shutdown();
    }


    public static void drawValue(char player, int[][] array) throws IOException {
//        System.out.println(Thread.currentThread().getName());
        int max = 6;
        int min = 1;
        Random rand = new Random();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        if(player == 'A') {
            snackLadder(countA, countB, randomNum, true, array);
        }else {
            snackLadder(countA, countB, randomNum, false, array);
        }

    }

    public static void snackLadder(int currentValueA, int currentValueB, int num, boolean value, int[][] array) throws IOException {

        List<Integer> snakeTail = new ArrayList<>();
        List<Integer> snakeHead = new ArrayList<>();
        List<Integer> ladderTop = new ArrayList<>();
        List<Integer> ladderBottom = new ArrayList<>();
//        System.out.println("length::" + array.length);

        for(int i =0; i < array.length; i++) {
           snakeTail.add(array[i][0]);
           snakeHead.add(array[i][1]);
           ladderTop.add(array[i][2]);
           ladderBottom.add(array[i][3]);
        }

        if(value) {
            if (currentValueA + num <= 100) {
//                Optional<Integer> sTop = snakeTop.stream().filter((val) -> val == currentValueA + num).findFirst();
//                Optional<Integer> lBottom = ladderBottom.stream().filter((val) -> val == currentValueA + num).findFirst();
//                if(sTop.isPresent()) {
                int sTopIndex = snakeHead.indexOf(currentValueA + num);
                int lBottomIndex = ladderBottom.indexOf(currentValueA + num);
                if(sTopIndex != -1) {
                    Integer snakeBottomValue = snakeTail.get(sTopIndex);
                    countA = snakeBottomValue;

                }else if(lBottomIndex != -1) {
                    Integer ladderTopValue = ladderTop.get(lBottomIndex);
                    countA = ladderTopValue;
                }
                else {
                    countA = currentValueA + num;
                }
                userAList.add(countA);
            }
        }else {
            if (currentValueB + num <= 100) {
                int sTopIndex = snakeHead.indexOf(currentValueB + num);
                int lBottomIndex = ladderBottom.indexOf(currentValueB + num);
                if(sTopIndex > -1) {
                    Integer snakeBottomValue = snakeTail.get(sTopIndex);
                    countB = snakeBottomValue;

                }else if(lBottomIndex > -1) {
                    Integer ladderTopValue = ladderTop.get(lBottomIndex);
                    countB = ladderTopValue;
                }
                else {
                    countB = currentValueB + num;
                }
                userBList.add(countB);
            }
        }
    }

    public static void printArray() {
        System.out.println("Player A");
        userAList.forEach((a) ->  System.out.print(a + " "));
        System.out.println();
        System.out.println("Player B");
        userBList.forEach((a) -> System.out.print(a + " "));
        System.out.println();
    }
}


//inputs are:
//4
//        1 27 3 22 4 17 5 8 9 21 11 26 7 19 20 29