package es.iesnervion.avazquez.juegocasillasynumeros.Clases;

public class Tablero {
    private int lado;
    private Casilla[][] casillas;
    private int[] marcasHorizontales;
    private int[] marcasVerticales;     //no es estrictamente necesario pero me resulta mas comodo

    //asi hago los maximosVerticales una propiedad derivada, ya que solo tendria que sumar los
    //puntos colocados para los maximos horizontales

    public Tablero(int lado) {
        this.lado = lado;
        casillas = new Casilla[lado][lado];

        //inicializar array de casillas
        for (int iRow = 0; iRow <lado; iRow++) {
            for (int iCol = 0; iCol < lado; iCol++) {
                    casillas[iRow][iCol] = new Casilla();

            }}

                //colocar maximos horizontales
        //colocar maximos verticales

        marcasHorizontales = new int[lado-1];
        marcasVerticales = new int[lado-1];

    }

    public int getLado() {
        return lado;
    }

    public Casilla[][] getCasillas() {
        return casillas;
    }

    public int[] getMarcasHorizontales() {
        return marcasHorizontales;
    }

    public void setMarcasHorizontales(int[] marcasHorizontales) {
        this.marcasHorizontales = marcasHorizontales;
    }

    public int[] getMarcasVerticales() {
        return marcasVerticales;
    }

    public void setMarcasVerticales(int[] marcasVerticales) {
        this.marcasVerticales = marcasVerticales;
    }
}
