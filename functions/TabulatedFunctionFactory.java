package functions;

//интерфейс фабрики табулированных функций
public interface TabulatedFunctionFactory {
    //создает табулированную функцию по границам и количеству точек
    TabulatedFunction createTabulatedFunction(double leftX, double rightX, int pointsCount);
    TabulatedFunction createTabulatedFunction(double leftX, double rightX, double[] values);//по границам и массиву значений
    TabulatedFunction createTabulatedFunction(FunctionPoint[] points);//по массиву точек
}