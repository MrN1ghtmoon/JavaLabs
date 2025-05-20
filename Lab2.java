package LabsForJava;

import java.util.*;

public class Lab2 {
    public class Main {

        public static String maxStr(String s) {
            if (s == null || s.isEmpty()) return "";
            int start = 0, maxLength = 0, maxStart = 0;
            int[] charIndex = new int[256];

            for (int end = 0; end < s.length(); end++) {
                char c = s.charAt(end);
                if (charIndex[c] >= start) {
                    start = charIndex[c] + 1;
                }
                charIndex[c] = end;
                if (end - start + 1 > maxLength) {
                    maxLength = end - start + 1;
                    maxStart = start;
                }
            }

            return s.substring(maxStart, maxStart + maxLength);
        }

        public static int[] arraysCombining(int[] arr1, int[] arr2) {
            int[] result = new int[arr1.length + arr2.length];
            int i = 0, j = 0, k = 0;

            while (i < arr1.length && j < arr2.length) {
                if (arr1[i] < arr2[j]) {
                    result[k++] = arr1[i++];
                } else {
                    result[k++] = arr2[j++];
                }
            }

            while (i < arr1.length) {
                result[k++] = arr1[i++];
            }

            while (j < arr2.length) {
                result[k++] = arr2[j++];
            }

            return result;
        }

        public static int maxSumArray(int[] nums) {
            if (nums == null || nums.length == 0) return 0;

            int maxSum = nums[0];
            int currentSum = nums[0];

            for (int i = 1; i < nums.length; i++) {
                currentSum = Math.max(nums[i], currentSum + nums[i]);
                maxSum = Math.max(maxSum, currentSum);
            }

            return maxSum;
        }

        public static int[][] rotateRight90(int[][] matrix) {
            if (matrix == null || matrix.length == 0) return new int[0][0];

            int n = matrix.length;
            int[][] rotated = new int[n][n];

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    rotated[j][n - 1 - i] = matrix[i][j];
                }
            }

            return rotated;
        }

        public static int[] paraWithSum9(int[] nums, int target) {
            for (int i = 0; i < nums.length; i++) {
                for (int j = i + 1; j < nums.length; j++) {
                    if (nums[i] + nums[j] == target) {
                        return new int[]{nums[i], nums[j]};
                    }
                }
            }

            return null;
        }

        public static int sumElemMatrix(int[][] arr) {
            int sum = 0;
            for (int[] row : arr) {
                for (int num : row) {
                    sum += num;
                }
            }
            return sum;
        }

        public static int[] maxString(int[][] arr) {
            if (arr == null || arr.length == 0) return new int[0];

            int[] maxValues = new int[arr.length];

            for (int i = 0; i < arr.length; i++) {
                if (arr[i] == null || arr[i].length == 0) {
                    maxValues[i] = Integer.MIN_VALUE;
                    continue;
                }

                int max = arr[i][0];
                for (int j = 1; j < arr[i].length; j++) {
                    if (arr[i][j] > max) {
                        max = arr[i][j];
                    }
                }
                maxValues[i] = max;
            }

            return maxValues;
        }


        public static int[][] rotateLeft90(int[][] matrix) {
            if (matrix == null || matrix.length == 0) return new int[0][0];

            int n = matrix.length;
            int[][] rotated = new int[n][n];

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    rotated[n - 1 - j][i] = matrix[i][j];
                }
            }

            return rotated;
        }

        public static void main(String[] args) {
            System.out.println("1. Наибольшая подстрока без повторений: " +
                    maxStr("aaaacabgnm"));

            int[] arr1 = {11, 33, 54};
            int[] arr2 = {18, 42, 78};
            System.out.println("2. Объединение массивов: " +
                    Arrays.toString(arraysCombining(arr1, arr2)));

            int[] nums = {-2, 1, -3, 4, -1, 2, 1, -5, 4};
            System.out.println("3. Максимальная сумма подмассива: " + maxSumArray(nums));

            int[][] matrix = {
                    {4, 4, 17},
                    {4, 4, 17},
                    {4, 4, 17}
            };
            System.out.println("4. Поворот на 90° по часовой:");
            printMatrix(rotateRight90(matrix));

            int[] nums2 = {2, 7, 11, 15};
            int target = 9;
            System.out.println("5. Пара с суммой 9: " +
                    Arrays.toString(paraWithSum9(nums2, target)));


            System.out.println("6. Сумма элементов матрицы: " + sumElemMatrix(matrix));

            System.out.println("7. Максимумы строк: " +
                    Arrays.toString(maxString(matrix)));

            System.out.println("8. Поворот на 90° против часовой:");
            printMatrix(rotateLeft90(matrix));
        }

        private static void printMatrix(int[][] matrix) {
            for (int[] row : matrix) {
                System.out.println(Arrays.toString(row));
            }
        }
    }
}
