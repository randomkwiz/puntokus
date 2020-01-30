package es.iesnervion.avazquez.puntokus.util;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import es.iesnervion.avazquez.puntokus.R;
import es.iesnervion.avazquez.puntokus.entities.Casilla;
import es.iesnervion.avazquez.puntokus.entities.Tablero;


public class Utilidad {

    /*
    *     final int EASY = 5;
    final int NORMAL = 9 ;
    final int HARD = 11 ;
    final int SICK = 15 ;
    * */
    public String getLevelName(int lados){
        String levelName = "";
        switch (lados){
            case 5:
                levelName = "EASY";
                break;
            case 9:
                levelName = "NORMAL";
                break;
            case 11:
                levelName = "HARD";
                break;
            case 15:
                levelName = "SICK";
                break;
        }
        return levelName;
    }


    /*
     * Signatura: public void establecerCasillasJugablesTablero(Tablero tablero)
     * Comentario: establece cuales son las casillas jugables en el tablero.
     *              Varían según el lado del tablero.
     *               Las casillas jugables son las que no están ni en la fila 0 ni en
     *               la columna 0.
     * Precondiciones: el tablero debe tener los lados establecidos.
     * Entradas: objeto tablero
     * Salidas: nada
     * Postcondiciones: las casillas del tablero quedarán establecidas como jugables o no jugables según
     * su posición en el mismo.
     * */
    public void establecerCasillasJugablesTablero(Tablero tablero) {
        int contador = 0;   //cuenta el numero de casillas en general
        for (int iRow = 0; iRow < tablero.getLado(); iRow++) {
            for (int iCol = 0; iCol < tablero.getLado(); iCol++) {
                contador++;
                if (contador <= tablero.getLado()) {

                    //Estas casillas son donde se ponen los numeros
                    tablero.getCasillas()[iRow][iCol].setJugable(false);

                } else if ((contador - 1) % tablero.getLado() == 0) { //er gitaneo weno pa que me pille la primera casilla de cada fila

                    //Estas casillas son donde se ponen los numeros
                    tablero.getCasillas()[iRow][iCol].setJugable(false);
                } else {

                    //Estas casillas son donde se ponen las marcas
                    tablero.getCasillas()[iRow][iCol].setJugable(true);

                }
            }
        }
    }

    /*
     * Signatura: public void establecerNumeroDeMarcasHorizontalesYVerticales(Tablero tablero)
     * Comentario: Cuenta las marcas pre establecidas del tablero y almacena en las propiedades
     *              marcasHorizontales y marcasVerticales del tablero, las que serán
     *               las marcas horizontales y verticales que indicarán los números de puntos
     *               que irán colocados en cada fila y columna.
     * Precondiciones: Ya debe haberse producido la repartición de marcas por el tablero.
     * Entradas: objeto tablero
     * Salidas: nada
     * Postcondiciones: el objeto tablero tendrá sus arrays de numeros horizontales y
     *                   verticales relleno y será una combinación resoluble.
     * */
    public void establecerNumeroDeMarcasHorizontalesYVerticales(Tablero tablero) {
        int[] row = new int[tablero.getMarcasHorizontales().length];
        int[] col = new int[tablero.getMarcasVerticales().length];
        int contadorRow = 0;
        int contadorCol = 0;
        int indice = 0;

        for (int iCol = 1; iCol < tablero.getLado(); iCol++) {
            for (int iRow = 1; iRow < tablero.getLado(); iRow++) {
                if (tablero.getCasillas()[iCol][iRow].isJugable() &&
                        tablero.getCasillas()[iCol][iRow].isMarcada()
                ) {
                    contadorCol++;
                }
            }
            col[indice] = contadorCol;
            contadorCol = 0;
            indice++;
        }


        indice = 0;
        //Ahora igual pero al reves
        for (int iRow = 1; iRow < tablero.getLado(); iRow++) {
            for (int iCol = 1; iCol < tablero.getLado(); iCol++) {
                if (tablero.getCasillas()[iCol][iRow].isJugable() &&
                        tablero.getCasillas()[iCol][iRow].isMarcada()
                ) {
                    contadorRow++;
                }
            }
            row[indice] = contadorRow;
            contadorRow = 0;
            indice++;
        }


        //Establezco las marcas
        tablero.setMarcasHorizontales(col);
        tablero.setMarcasVerticales(row);
    }


    /*
     * Signatura: public void establecerMarcasEnTablero(Tablero tablero)
     * Comentario: Reparte las marcas por las casillas jugables del tablero.
     * Precondiciones: Las casillas deben estar marcadas como jugables o no jugables según
     *               corresponda por su posición en el tablero.
     * Entradas: Objeto tablero
     * Salidas: nada
     * Postcondiciones: Las marcas quedarán repartidas por las casillas jugables del tablero.
     * */

    //Sirve para después contarlas y así asegurarme de que el juego es resoluble.
    public void establecerMarcasEnTablero(Tablero tablero) {
        for (int iCol = 1; iCol < tablero.getLado(); iCol++) {
            for (int iRow = 1; iRow < tablero.getLado(); iRow++) {

                if (tablero.getCasillas()[iCol][iRow].isJugable()) {
                    if (marcoONoMarco()) {
                        tablero.getCasillas()[iCol][iRow].setMarcada(true);
                    }else{
                        //Añado esta linea para que me sirva para crear nueva partida
                        //(si no, se me acumulan las marcas de una vez para otra)
                        tablero.getCasillas()[iCol][iRow].setMarcada(false);
                    }
                }
            }
        }
    }


    /*
     * Signatura: public void cambiarValoresEnLaVista(Context context, Tablero tablero )
     * Comentario: Este método actualiza en
     *               la interfaz de usuario los valores de las casillas no jugables.
     * Precondiciones:
     * Entradas: ConstraintLayout donde se actualizarán los valores y objeto tablero
     * Salidas:
     * Postcondiciones: Los valores de las casillas no jugables de la
     *   interfaz de usuario quedarán actualizados con los valores
     *   actuales del objeto tablero.
     * */
    public void cambiarValoresEnLaVista(ConstraintLayout layout, Tablero tablero) {


        View view;
        int contador = 0;
        int contadorRow = 0;
        int contadorCol = 0;


        for (int iRow = 0; iRow < tablero.getLado(); iRow++) {
            for (int iCol = 0; iCol < tablero.getLado(); iCol++) {
                contador++;

                if (iRow != 0 || iCol != 0) {  //para que no escriba justo en la posicion 0,0
                    if (!tablero.getCasillas()[iRow][iCol].isJugable()) {
                        if (contador <= tablero.getLado()) {
                            view = layout.getViewById(tablero.getCasillas()[iRow][iCol].getId());
                            ((TextView) view).setText(String.valueOf(tablero.getMarcasHorizontales()[contadorCol]));
                            //((TextView)view).setText("H");
                            contadorCol++;

                            ((TextView) view).setTextColor(Color.BLACK);
                            ((TextView) view).setGravity(Gravity.CENTER);

                        } else if ((contador - 1) % tablero.getLado() == 0) {
                            view = layout.getViewById(tablero.getCasillas()[iRow][iCol].getId());

                            ((TextView) view).setTextColor(Color.BLACK);
                            ((TextView) view).setText(String.valueOf(tablero.getMarcasVerticales()[contadorRow]));
                            contadorRow++;

                        }
                    }
                }


            }
        }


    }


    /*
     * Signatura: private boolean marcoONoMarco()
     * Comentario: Metodo que devuelve de manera pseudo aleatoria
     *            un boolean.
     * Precondiciones:
     * Entradas:
     * Salidas:
     * Postcondiciones: Asociado al nombre devuelve un boolean que obtendrá los valores de forma
     *                   pseudo aleatoria.
     *                   Posee un 50% de posibilidades de ser true y un 50% de posibilidades
     *                   de ser false.
     * */
    private boolean marcoONoMarco() {
        final int MAX = 2;
        int numeroAleatorio = (int) (Math.random() * MAX + 1); //entre 1 y 2
        boolean marcar = false;
        if (numeroAleatorio == 1) {
            marcar = true;
        }

        return marcar;
    }

    /*
     * Signatura: public Casilla obtenerCasillaPorID(Tablero tablero, int id)
     * Comentario: devuelve el objeto casilla asociado a la ID de la vista correspondiente.
     * Precondiciones: El tablero debe estar establecido con las ID de las
     *               vistas generadas.
     * Entradas: Objeto tablero y entero ID.
     * Salidas: objeto Casilla
     * Postcondiciones: Asociado al nombre se devuelve el objeto casilla que corresponda
     *                   con el ID de la vista asociada, que a su vez será el mismo
     *                   que el ID de la propia casilla.
     *
     * */
    public Casilla obtenerCasillaPorID(Tablero tablero, int id) {
        Casilla casilla = null;
        for (int iRow = 0; iRow < tablero.getLado(); iRow++) {
            for (int iCol = 0; iCol < tablero.getLado(); iCol++) {

                if (tablero.getCasillas()[iRow][iCol].getId() == id) {
                    casilla = tablero.getCasillas()[iRow][iCol];
                }
            }
        }
        return casilla;
    }

    /*
     * Signatura: public boolean comprobarSiLaSolucionEsCorrecta(Tablero tablero)
     * Comentario: Comprueba si la solución que se muestra en pantalla es correcta.
     * Precondiciones: Objeto tablero con las marcas numéricas establecidas
     * Entradas: Objeto tablero
     * Salidas: boolean
     * Postcondiciones: Asociado al nombre se devuelve true si la solución es correcta y false si no.
     *
     * */
    public boolean comprobarSiLaSolucionEsCorrecta(Tablero tablero) {
        boolean isCorrectCol = false;
        boolean isCorrectRow = false;
        boolean isCorrecto = true;

        for (int i = 1; i < tablero.getLado(); i++) {
            isCorrectCol = comprobarColumnaCorrecta(tablero, i);
            isCorrectRow = comprobarFilaCorrecta(tablero, i);

            if (!isCorrectCol || !isCorrectRow) { //si alguna de las dos es incorrecta
                isCorrecto = false;
            }
        }

        return isCorrecto;
    }

    /*
     * Signatura: public boolean comprobarFilaCorrecta(Tablero tablero, int nRow)
     * Comentario: Comprueba si la distribución de puntos de la fila es congruente con la marca
     *              numerica vertical corespondiente.
     * Precondiciones: Objeto tablero con las marcas numéricas establecidas y numero de fila a comprobar
     * Entradas: Objeto tablero, entero numero de fila
     * Salidas: boolean
     * Postcondiciones: Asociado al nombre se devuelve true si la distribucion de puntos es igual a la cantidad indicada
     *      y false si no.
     * */
    public boolean comprobarFilaCorrecta(Tablero tablero, int nRow) {
        boolean isCorrecta = false;
        int contadorRow = 0;


        for (int iCol = 1; iCol < tablero.getLado(); iCol++) {
            if (tablero.getCasillas()[nRow][iCol].isJugable()) {
                //Cuento las marcas que hay en la columna "nCol"
                if (tablero.getCasillas()[nRow][iCol].getImgSrc() == R.drawable.selecteditem) {
                    contadorRow++;
                }

            }
        }
        //Si las marcas son las mismas que para su cabecera, esta columna esta correcta
        if (tablero.getMarcasVerticales()[nRow - 1] == contadorRow) {
            isCorrecta = true;
        }

        return isCorrecta;
    }


    /*
     * Signatura: public boolean comprobarColumnaCorrecta(Tablero tablero, int nCol)
     * Comentario: Comprueba si la distribución de puntos de la columna es congruente con la marca
     *              numerica horizontal corespondiente.
     * Precondiciones: Objeto tablero con las marcas numéricas establecidas y numero de columna a comprobar
     * Entradas: Objeto tablero, entero numero de columna
     * Salidas: boolean
     * Postcondiciones: Asociado al nombre se devuelve true si la distribucion de puntos es igual a la cantidad indicada
     *      y false si no.
     * */
    public boolean comprobarColumnaCorrecta(Tablero tablero, int nCol) {
        boolean isCorrecta = false;
        int contadorCol = 0;


        for (int iRow = 1; iRow < tablero.getLado(); iRow++) {
            if (tablero.getCasillas()[iRow][nCol].isJugable()) {
                //Cuento las marcas que hay en la fila "nRow"
                if (tablero.getCasillas()[iRow][nCol].getImgSrc() == R.drawable.selecteditem) {
                    contadorCol++;
                }

            }
        }
        //Si las marcas son las mismas que para su cabecera, esta fila esta correcta
        if (tablero.getMarcasHorizontales()[nCol - 1] == contadorCol) {
            isCorrecta = true;
        }

        return isCorrecta;
    }


    /*
    * Signatura: public void limpiarPuntosNegrosVisualesDeLaVista(Tablero tablero)
    * Comentario: Pasa todos los atributos imgSrc de las casillas jugables a nonselecteditem,
    *               marcando así las casillas como no marcadas. Cambia el image rsource de las vistas
    *               correspondientes.
    * Precondiciones: tablero con casillas jugables y no jugables establecidas
    * Entrada: tablero
    * Salidas:
    * Postcondiciones: Pasa todos los atributos imgSrc de las casillas jugables a nonselecteditem
    * */
    public void limpiarPuntosNegrosVisualesDeLaVista(Tablero tablero, ConstraintLayout layout){
        View casilla;
        for (int iRow = 0; iRow < tablero.getLado(); iRow++) {
            for (int iCol = 0; iCol < tablero.getLado(); iCol++) {
                casilla = layout.getViewById(tablero.getCasillas()[iRow][iCol].getId());
                if(casilla instanceof ImageView && tablero.getCasillas()[iRow][iCol].isJugable()){
                    casilla = (ImageView) layout.getViewById(tablero.getCasillas()[iRow][iCol].getId());
                    if(tablero.getCasillas()[iRow][iCol].getImgSrc() == R.drawable.selecteditem){
                        ((ImageView) casilla).setImageResource(R.drawable.nonselecteditem);
                        casilla.setTag(R.drawable.nonselecteditem);

                        tablero.getCasillas()[iRow][iCol].setMarcada(false);
                        tablero.getCasillas()[iRow][iCol].setImgSrc(R.drawable.nonselecteditem);

                    }
                }



            }
        }
    }

    /*
    * Signatura: public void partidaNueva(Tablero tablero, ConstraintLayout layout)
    * Comentario: Empieza una nueva partida
    * Precondiciones:
    * Entradas: Tablero y Constraint layout
    * Salidas:
    * Postcondiciones: se empezara una partida nueva, reiniciandose el tablero y limpiandose las marcas
    * */
    public void partidaNueva(Tablero tablero, ConstraintLayout layout){

        //establezco cuales son las casillas jugables
        establecerCasillasJugablesTablero(tablero);


        //pone las marcas (esto el usuario no lo ve, es mi forma de hacer los numeritos)
        establecerMarcasEnTablero(tablero);

        //se cuentan los numeritos xD
        establecerNumeroDeMarcasHorizontalesYVerticales(tablero);


        //Actualizo los valores de la vista
        cambiarValoresEnLaVista(layout, tablero);

        //Limpio las marcas visuales (puntos negros) de la vista
        limpiarPuntosNegrosVisualesDeLaVista(tablero, layout);

    }

    /*
    * Muestra un Toast
    * */
    public void mostrarToast(String mensaje, Context context){
        Toast toast =
                Toast.makeText(context,
                        mensaje, Toast.LENGTH_SHORT);
        toast.show();
    }
}
