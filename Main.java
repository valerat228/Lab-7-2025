import functions.FunctionPoint;
import functions.ArrayTabulatedFunction;
import functions.LinkedListTabulatedFunction;
import functions.TabulatedFunction;
import functions.InappropriateFunctionPointException;
import functions.Function;
import functions.Functions;
import functions.TabulatedFunctions;
import functions.basic.*;
import functions.meta.*;
import java.io.*;


public class Main {
    private static void nonThread() {
        //создаем объект задания
        threads.Task task = new threads.Task();
        task.setTasksCount(100);

        //цикл по всем заданиям
        for (int i = 0; i < task.getTasksCount(); i++) {
            try {
                //создаем логарифмическую функцию со случайным основанием от 1 до 10
                double base = 1 + Math.random() * 9; //случайное число от 1 до 10
                functions.basic.Log logFunc = new functions.basic.Log(base);
                task.setFunction(logFunc);

                //левая граница от 0 до 100
                double left = Math.random() * 100;
                task.setLeftBorder(left);

                //правая граница от 100 до 200
                double right = 100 + Math.random() * 100;
                task.setRightBorder(right);

                //шаг дискретизации от 0 до 1
                double step = Math.random();
                task.setDiscretizationStep(step);

                System.out.printf("Source %.3f %.3f %.3f%n", left, right, step);

                double result = Functions.integrate(logFunc, left, right, step);

                System.out.printf("Result %.3f %.3f %.3f %.6f%n", left, right, step, result);

            } catch (IllegalArgumentException e) {
                System.out.println("ошибка: " + e.getMessage());
            }
        }
    }

    private static void simpleThreads() {
        //создаем объект задания
        threads.Task task = new threads.Task();
        task.setTasksCount(100);

        //создаем потоки
        Thread generatorThread = new Thread(new threads.SimpleGenerator(task));
        Thread integratorThread = new Thread(new threads.SimpleIntegrator(task));

        //запускаем потоки
        integratorThread.start();
        generatorThread.start();



        //ждем завершения потоков
        try {
            generatorThread.join();
            integratorThread.join();
        } catch (InterruptedException e) {
            System.out.println("основной поток прерван");
        }
    }

    private static void complicatedThreads() {
        //создаем объекты
        threads.Task task = new threads.Task();
        task.setTasksCount(100);

        threads.ReadWriteSemaphore semaphore = new threads.ReadWriteSemaphore();

        //создаем потоки
        threads.Generator generator = new threads.Generator(task, semaphore);
        threads.Integrator integrator = new threads.Integrator(task, semaphore);

        //устанавливаем приоритеты
        generator.setPriority(Thread.MIN_PRIORITY);
        integrator.setPriority(Thread.MAX_PRIORITY);

        System.out.println("Запускаем потоки");
        generator.start();
        integrator.start();

        //ждём 50 мс
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            System.out.println("Основной поток прерван");
        }

        //прерываем потоки
        System.out.println("\nПрерываю потоки");
        generator.interrupt();
        integrator.interrupt();

        try {
            generator.join();
            integrator.join();
        } catch (InterruptedException e) {
            System.out.println("Основной поток прерван");
        }
    }

    public static void main(String[] args) throws IOException, InappropriateFunctionPointException {

        System.out.println("\n" + "=".repeat(50));
        System.out.println("=== ТЕСТЫ ДЛЯ ЛАБОРАТОРНОЙ №6 ===");
        System.out.println("=".repeat(50));

        //(Задание 1)
        {
            System.out.println("\n=== ЗАДАНИЕ 1 ===");
            functions.basic.Exp expFunc = new functions.basic.Exp();
            double theoretical = Math.E - 1;
            System.out.printf("теоретическое значение ∫e^x dx от 0 до 1: %.10f%n", theoretical);

            //проверка работы метода
            double result = Functions.integrate(expFunc, 0, 1, 0.1);
            System.out.printf("результат при шаге 0.1: %.10f%n", result);

            //поиск шага для 7 знака
            System.out.println("\nпоиск шага для точности до 7 знака:");
            double step = 0.1;
            boolean found = false;

            for (int i = 0; i < 10 && found == false; i++) {
                result = Functions.integrate(expFunc, 0, 1, step);
                double error = Math.abs(result - theoretical);

                if (error < 1e-7) {
                    System.out.printf("шаг %.6f: ошибка %.10f (достигнут 7 знак)%n", step, error);
                    found = true;
                } else {
                    System.out.printf("шаг %.6f: ошибка %.10f%n", step, error);
                }

                step /= 2.0; //уменьшаем шаг в 2 раза
            }

            if (!found) {
                System.out.println("7 знак не достигнут (шаг меньше 0.0001)");
            }
        }

        //Задание 2
        {
            System.out.println("\n=== ЗАДАНИЕ 2 ===");
            nonThread();
        }

        //Задание 3
        {
            System.out.println("\n=== ЗАДАНИЕ 3 ===");
            simpleThreads();
        }

        //Задание 4
        {
            System.out.println("\n=== ЗАДАНИЕ 4 ===");
            complicatedThreads();
        }

        System.out.println("\n" + "=".repeat(50));
        System.out.println("=== ТЕСТЫ ДЛЯ ЛАБОРАТОРНОЙ №7 ===");
        System.out.println("=".repeat(50));

        System.out.println("\n=== ЗАДАНИЕ 1 ===");
        {
            System.out.println("\n1. ArrayTabulatedFunction:");
            TabulatedFunction arrayFunc = new ArrayTabulatedFunction(0, 10, 5);
            for (FunctionPoint p : arrayFunc) {
                System.out.println(p);
            }

            System.out.println("\n2. LinkedListTabulatedFunction:");
            TabulatedFunction listFunc = new LinkedListTabulatedFunction(0, 10, 5);
            for (FunctionPoint p : listFunc) {
                System.out.println(p);
            }
        }

        System.out.println("\n=== ЗАДАНИЕ 2 ===");
        {
            Function f = new Cos();
            TabulatedFunction tf;
            tf = TabulatedFunctions.tabulate(f, 0, Math.PI, 11);
            System.out.println(tf.getClass());
            TabulatedFunctions.setTabulatedFunctionFactory(new LinkedListTabulatedFunction.LinkedListTabulatedFunctionFactory());
            tf = TabulatedFunctions.tabulate(f, 0, Math.PI, 11);
            System.out.println(tf.getClass());
            TabulatedFunctions.setTabulatedFunctionFactory(new ArrayTabulatedFunction.ArrayTabulatedFunctionFactory());
            tf = TabulatedFunctions.tabulate(f, 0, Math.PI, 11);
            System.out.println(tf.getClass());
        }

        System.out.println("\n=== ЗАДАНИЕ 3 ===");
        {
            TabulatedFunction f;

            f = TabulatedFunctions.createTabulatedFunction(
                    ArrayTabulatedFunction.class, 0, 10, 3);
            System.out.println(f.getClass());
            System.out.println(f);

            f = TabulatedFunctions.createTabulatedFunction(
                    ArrayTabulatedFunction.class, 0, 10, new double[] {0, 10});
            System.out.println(f.getClass());
            System.out.println(f);

            f = TabulatedFunctions.createTabulatedFunction(
                    LinkedListTabulatedFunction.class,
                    new FunctionPoint[] {
                            new FunctionPoint(0, 0),
                            new FunctionPoint(10, 10)
                    }
            );
            System.out.println(f.getClass());
            System.out.println(f);

            f = TabulatedFunctions.tabulate(
                    LinkedListTabulatedFunction.class, new Sin(), 0, Math.PI, 11);
            System.out.println(f.getClass());
            System.out.println(f);

            //создаем и записываем тестовую функцию
            TabulatedFunction testFunc = TabulatedFunctions.createTabulatedFunction(
                    ArrayTabulatedFunction.class, 0, 10, 3);

            //тест inputTabulatedFunction
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            TabulatedFunctions.outputTabulatedFunction(testFunc, byteOut);
            ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
            TabulatedFunction read1 = TabulatedFunctions.inputTabulatedFunction(
                    LinkedListTabulatedFunction.class, byteIn);
            System.out.println("inputTabulatedFunction: " + read1.getClass().getSimpleName());

            //тест readTabulatedFunction
            StringWriter writer = new StringWriter();
            TabulatedFunctions.writeTabulatedFunction(testFunc, writer);
            StringReader reader = new StringReader(writer.toString());
            TabulatedFunction read2 = TabulatedFunctions.readTabulatedFunction(
                    ArrayTabulatedFunction.class, reader);
            System.out.println("readTabulatedFunction: " + read2.getClass().getSimpleName());
        }

            System.out.println("\n=== ВСЕ ТЕСТЫ ЗАВЕРШЕНЫ! ===");
    }


}