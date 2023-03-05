import java.util.*;
import java.util.Collections;
public class Main {

    //Số lượng giáo viên và số lượng tiết học trong một tuần
    private static final int NUM_TEACHERS = 10;
    private static final int NUM_SLOTS = 20;
    private static final int NUM_COURSES = 20;
    private static final int NUM_ROOMS = 5;
    private static final int NUM_DAYS = 5;
    private static final int NUM_SLOTS_PER_DAY = 8;

    //Số lượng cá thể trong quần thể
    private static final int POPULATION_SIZE = 50;

    //Tỉ lệ lai ghép và đột biến
    private static final double CROSSOVER_RATE = 0.8;
    private static final double MUTATION_RATE = 0.1;

    //Số lượng thế hệ tối đa trong thuật toán di truyền
    private static final int MAX_GENERATIONS = 500;

    //Mảng lưu trữ các tiết học trong một tuần
    private int[][] schedule;

    //Khởi tạo lịch biểu ban đầu
    private void initializeSchedule() {
        schedule = new int[NUM_TEACHERS][NUM_SLOTS];
        for (int i = 0; i < NUM_TEACHERS; i++) {
            for (int j = 0; j < NUM_SLOTS; j++) {
                schedule[i][j] = -1;
            }
        }
    }

    //Hàm đánh giá độ thích nghi của một cá thể
    private int evaluateFitness(int[] individual) {
        int fitness = 0;
        for (int i = 0; i < NUM_TEACHERS; i++) {
            List<Integer> assignedSlots = new ArrayList<>();
            for (int j = 0; j < NUM_SLOTS; j++) {
                if (individual[i * NUM_SLOTS + j] == 1) {
                    assignedSlots.add(j);
                }
            }
            //Kiểm tra số lượng tiết học của mỗi giáo viên trong một ngày
            int numClassesPerDay = 0;
            for (int j = 0; j < assignedSlots.size(); j++) {
                if (j == 0 || assignedSlots.get(j) - assignedSlots.get(j-1) > 1) {
                    numClassesPerDay++;
                }
            }
            fitness += numClassesPerDay;
        }
        return fitness;
    }
    // Hàm đánh giá độ thích nghi của một cá thể cải tiến
//    private int evaluateFitness(int[] individual) {
//        int numViolations = 0;
//        int[][][] schedule = new int[NUM_TEACHERS][NUM_DAYS][NUM_SLOTS_PER_DAY];
//        for (int i = 0; i < individual.length; i++) {
//            int teacher = i / (NUM_COURSES * NUM_ROOMS);
//            int course = (i % (NUM_COURSES * NUM_ROOMS)) / NUM_ROOMS;
//            int room = i % NUM_ROOMS;
//            int day = (i / NUM_ROOMS) % NUM_DAYS;
//            int slot = (i % NUM_SLOTS_PER_DAY) + (day * NUM_SLOTS_PER_DAY);
//            if (schedule[teacher][day][slot] != 0) {
//                numViolations++;
//            }
//            schedule[teacher][day][slot] = course * NUM_ROOMS + room + 1;
//        }
//        for (int day = 0; day < NUM_DAYS; day++) {
//            for (int slot = 0; slot < NUM_SLOTS_PER_DAY; slot++) {
//                List<Integer> usedTeachers = new ArrayList<>();
//                for (int teacher = 0; teacher < NUM_TEACHERS; teacher++) {
//                    int course = schedule[teacher][day][slot];
//                    if (course != 0) {
//                        if (usedTeachers.contains(teacher)) {
//                            numViolations++;
//                        } else {
//                            usedTeachers.add(teacher);
//                        }
//                    }
//                }
//            }
//        }
//        int numConstraints = NUM_TEACHERS * NUM_DAYS * NUM_SLOTS_PER_DAY + NUM_TEACHERS * (NUM_TEACHERS - 1) / 2;
//        return numConstraints - numViolations;
//    }

                    //Hàm lựa chọn cá thể trong quần thể
    private int[] selection(int[][] population, int[] fitness) {
        int totalFitness = 0;
        for (int i = 0; i < POPULATION_SIZE; i++) {
            totalFitness += fitness[i];
        }
        int randomValue = new Random().nextInt(totalFitness);
        int sum = 0;
        for (int i = 0; i < POPULATION_SIZE; i++) {
            sum += fitness[i];
            if (sum > randomValue) {
                return population[i];
            }
        }
        return population[0];
    }

    //Hàm lai ghép giữa hai cá thể
    private int[] crossover(int[] parent1, int[] parent2) {
        int crossoverPoint = new Random().nextInt(NUM_TEACHERS * NUM_SLOTS);
        int[] child = new int[NUM_TEACHERS * NUM_SLOTS];
        for (int i = 0; i < crossoverPoint; i++) {
            child[i] = parent1[i];
        }
        for (int i = crossoverPoint; i < NUM_TEACHERS * NUM_SLOTS; i++) {
            child[i] = parent2[i];
        }
        return child;
    }

    private int[] mutation(int[] individual) {
        int[] mutatedIndividual = Arrays.copyOf(individual, individual.length);
        int mutationIndex = new Random().nextInt(NUM_SLOTS * NUM_TEACHERS);
        int newValue = new Random().nextInt(NUM_COURSES * NUM_ROOMS);
        mutatedIndividual[mutationIndex] = newValue;
        return mutatedIndividual;
    }

    // lai ghép nhiều điểm cắt
    private int[] multiplePointCrossover(int[] parent1, int[] parent2) {
        int[] child1 = Arrays.copyOf(parent1, parent1.length);
        int[] child2 = Arrays.copyOf(parent2, parent2.length);
        int numCrossoverPoints = new Random().nextInt(NUM_SLOTS * NUM_TEACHERS / 2) + 1;
        int[] crossoverPoints = new Random().ints(numCrossoverPoints, 0, NUM_SLOTS * NUM_TEACHERS).sorted().toArray();
        boolean useParent1 = true;
        int lastCrossoverPoint = 0;
        for (int crossoverPoint : crossoverPoints) {
            if (useParent1) {
                for (int i = lastCrossoverPoint; i < crossoverPoint; i++) {
                    child2[i] = parent1[i];
                }
                for (int i = crossoverPoint; i < NUM_SLOTS * NUM_TEACHERS; i++) {
                    child1[i] = parent1[i];
                }
            } else {
                for (int i = lastCrossoverPoint; i < crossoverPoint; i++) {
                    child1[i] = parent2[i];
                }
                for (int i = crossoverPoint; i < NUM_SLOTS * NUM_TEACHERS; i++) {
                    child2[i] = parent2[i];
                }
            }
            useParent1 = !useParent1;
            lastCrossoverPoint = crossoverPoint;
        }
        if (new Random().nextDouble() < MUTATION_RATE) {
            child1 = mutation(child1);
        }
        if (new Random().nextDouble() < MUTATION_RATE) {
            child2 = mutation(child2);
        }
        return (evaluateFitness(child1) < evaluateFitness(child2)) ? child1 : child2;
    }

    //Lai ghép một điểm cắt
    private int[] onePointCrossover(int[] parent1, int[] parent2) {
        int crossoverPoint = new Random().nextInt(NUM_SLOTS * NUM_TEACHERS);
        int[] child1 = Arrays.copyOf(parent1, parent1.length);
        int[] child2 = Arrays.copyOf(parent2, parent2.length);
        for (int i = crossoverPoint; i < NUM_SLOTS * NUM_TEACHERS; i++) {
            int temp = child1[i];
            child1[i] = child2[i];
            child2[i] = temp;
        }
        if (new Random().nextDouble() < MUTATION_RATE) {
            child1 = mutation(child1);
        }
        if (new Random().nextDouble() < MUTATION_RATE) {
            child2 = mutation(child2);
        }
        return (evaluateFitness(child1) < evaluateFitness(child2)) ? child1 : child2;
    }

    //Hàm đột biến trên một cá thể
    private void mutate(int[] individual) {
        int mutationPoint = new Random().nextInt(NUM_TEACHERS * NUM_SLOTS);
        individual[mutationPoint] = individual[mutationPoint] == 0 ? 1 : 0;
    }

    // đột bieesn ngẫu nhiên
    private int[] randomMutation(int[] individual) {
        int[] mutatedIndividual = Arrays.copyOf(individual, individual.length);
        int mutationIndex = new Random().nextInt(NUM_SLOTS * NUM_TEACHERS);
        int newValue = new Random().nextInt(NUM_COURSES * NUM_ROOMS);
        mutatedIndividual[mutationIndex] = newValue;
        return mutatedIndividual;
    }
    //đột biến đổi chỗ
    private int[] swapMutation(int[] individual) {
        int[] mutatedIndividual = Arrays.copyOf(individual, individual.length);
        int mutationIndex1 = new Random().nextInt(NUM_SLOTS * NUM_TEACHERS);
        int mutationIndex2 = new Random().nextInt(NUM_SLOTS * NUM_TEACHERS);
        int temp = mutatedIndividual[mutationIndex1];
        mutatedIndividual[mutationIndex1] = mutatedIndividual[mutationIndex2];
        mutatedIndividual[mutationIndex2] = temp;
        return mutatedIndividual;
    }


    //Hàm tạo một thế hệ mới
    private int[][] createNextGeneration(int[][] population, int[] fitness) {
        int[][] newPopulation = new int[POPULATION_SIZE][NUM_TEACHERS * NUM_SLOTS];
        for (int i = 0; i < POPULATION_SIZE; i++) {
            int[] parent1 = selection(population, fitness);
            int[] parent2 = selection(population, fitness);
            int[] child = parent1;
            if (new Random().nextDouble() < CROSSOVER_RATE) {
                child = crossover(parent1, parent2);
            }
            if (new Random().nextDouble() < MUTATION_RATE) {
                mutate(child);
            }
            newPopulation[i] = child;
        }
        return newPopulation;
    }

    //Hàm chạy thuật toán di truyền để lập lịch thời khóa biểu
    public void run() {
        initializeSchedule();

        //Khởi tạo quần thể ban đầu
        int[][] population = new int[POPULATION_SIZE][NUM_TEACHERS * NUM_SLOTS];
        for (int i = 0; i < POPULATION_SIZE; i++) {
            for (int j = 0; j < NUM_TEACHERS * NUM_SLOTS; j++) {
                population[i][j] = new Random().nextInt(2);
            }
        }

        //Chạy thuật toán di
        int generation = 0;
        while (generation < MAX_GENERATIONS) {
            int[] fitness = new int[POPULATION_SIZE];
            for (int i = 0; i < POPULATION_SIZE; i++) {
                fitness[i] = evaluateFitness(population[i]);
            }
//            int bestFitness = Collections.min(Arrays.asList(fitness));
            int bestFitness = fitness[0];
            for (int i = 1; i < POPULATION_SIZE; i++) {
                if (fitness[i] < bestFitness) {
                    bestFitness = fitness[i];
                }
            }
            if (bestFitness == 0) {
                System.out.println("Solution found after " + generation + " generations.");
                printSchedule(population[Arrays.asList(fitness).indexOf(bestFitness)]);
                return;
            }
            population = createNextGeneration(population, fitness);
            generation++;
        }
        System.out.println("No solution found after " + MAX_GENERATIONS + " generations.");
    }

    //Hàm in ra lịch biểu
    private void printSchedule(int[] individual) {
        for (int i = 0; i < NUM_TEACHERS; i++) {
            System.out.print("Teacher " + i + ": ");
            for (int j = 0; j < NUM_SLOTS; j++) {
                if (individual[i * NUM_SLOTS + j] == 1) {
                    System.out.print(j + " ");
                }
            }
            System.out.println();
        }
    }

    //Hàm main để thực thi chương trình
    public static void main(String[] args) {
        Main scheduler = new Main();
        scheduler.run();
    }
}

