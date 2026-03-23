package com.example.turingmachinesimulator_2026;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.text.Layout;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    String inputAnterior;
    int contadorShot = 0;
    boolean returnProgama = false;
    ArrayList<String> arregloPrincipal = new ArrayList<>();
    int palabraActual = 0;
    int ultimoElemento = 0;
    int READ_REQUEST_CODE = 123;
    int WRITE_REQUEST_CODE = 678;
    MyEditText input;
    ImageButton btnStart;
    ImageButton btnImportar;
    ArrayList<ArrayList<String>> programa = new ArrayList<ArrayList<String>>();
    List<String> reservedWords = new ArrayList<>();
    List<String> actions = new ArrayList<>();
    ArrayList<Object[]> programaI = new ArrayList<>();
    int posicionArregloActual;
    EditText inputEntrada;
    TimerTask timerTask;
    Timer timer;
    Object[] palabraActual2;
    ImageButton btnNext;
    ImageButton btnStop;
    ImageButton btnContinue;
    ScrollView scrollViewVertical;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Inicializar interfaz
        input = findViewById(R.id.input);
        btnStart = findViewById(R.id.btnStart);
        btnNext = findViewById(R.id.btnNext);
        btnContinue = findViewById(R.id.btnContinue);
        btnStop = findViewById(R.id.btnStop);
        inputEntrada = findViewById(R.id.inputEntrada);
        ImageButton btnExportar = findViewById(R.id.btnExportar);
        btnImportar = findViewById(R.id.btnImportar);
        ImageButton btnAbrirDialogo = findViewById(R.id.btnAbrirDialogo);
        scrollViewVertical = findViewById(R.id.scrollViewVertical);

        btnNext.setEnabled(false);
        btnContinue.setEnabled(false);
        btnStop.setEnabled(false);

        posicionArregloActual = 0;

        //Listeners
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargarPrograma();
                btnNext.setEnabled(false);
                btnContinue.setEnabled(false);
                btnStop.setEnabled(true);
                btnStart.setEnabled(false);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timer.cancel();
                btnNext.setEnabled(true);
                btnContinue.setEnabled(true);
                btnStart.setEnabled(true);
                btnStop.setEnabled(false);
            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                timer = new Timer();

                timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                animarPrograma();
                            }
                        });
                    }
                };

                timer.schedule(timerTask, 0, 600);

                btnStart.setEnabled(false);
                btnContinue.setEnabled(false);
                btnNext.setEnabled(false);
                btnStop.setEnabled(true);
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animarPrograma();
            }
        });

        btnImportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirSelectorArchivos();
            }
        });


        btnExportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirDialogoGuardarArchivo();
            }
        });

        //Inicializar variables
        actions.add("Return");
        actions.add("Write");
        actions.add("Move");
        actions.add("Goto");

        reservedWords.add("If");
        reservedWords.add("Blank");
        reservedWords.add("Return");
        reservedWords.add("False");
        reservedWords.add("True");
        reservedWords.add("Write");
        reservedWords.add("Not");
        reservedWords.add("Move");
        reservedWords.add("Right");
        reservedWords.add("Move");
        reservedWords.add("Left");
        reservedWords.add("Goto");

        btnAbrirDialogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarDialogo("Authors", "Polytechic University of Victoria");
            }
        });
    }

    private int cargarPrograma(){
        contadorShot = 0;
        returnProgama = false;
        arregloPrincipal.clear();
        palabraActual = 0;
        ultimoElemento = 0;
        programa.clear();
        programaI.clear();

        String texto = input.getText().toString();
        texto = texto.replaceAll("(?m)^[ \t]*\r?\n", "");
        input.setText(texto);

        //Eliminar espacios en blanco
        texto = texto.trim();
        input.setText(texto);

        if (texto.replaceAll(" ", "").isEmpty()) {
            return -1;
        }

        String[] lineas = texto.split("\n");

        //Obtener cada arreglo de linea en arrego de palabras
        for (String linea : lineas) {
            String[] palabras = linea.split(" ");
            ArrayList<String> lineaPalabras = new ArrayList<String>(Arrays.asList(palabras));
            programa.add(lineaPalabras);
        }

        //Obtener todas las palabras en un arreglo, su linea y su número de palabra.
        int contador = 0;

        for (int indexLine = 0; indexLine < programa.size(); indexLine++) {
            ArrayList<String> line = programa.get(indexLine);

            for (String word : line) {
                programaI.add(new Object[] { word, indexLine, contador });
                contador++;
            }
        }

        // Obtener la última línea
        int ultimaLinea = (int) programaI.get(programaI.size() - 1)[1];

        // Obtener el último elemento
        int ultimoElemento = programaI.size();

        for (int i = ultimoElemento; i < ultimoElemento + 10; i++) {
            programaI.add(new Object[] { "        ", ultimaLinea, i });
        }

        List<Object> valor = verifyLanguage(programa, reservedWords);

        if (programaI.get(programaI.size() - 12)[0].toString().trim().equals("Goto") || programaI.get(programaI.size() - 12)[0].toString().trim().equals("Return")) {
        } else {
            valor = Arrays.asList("Error en la última línea: El programa debe terminar con una instrucción Return o Goto", ultimaLinea);
        }

        if (valor == null){
            valor = verifySintaxis();
            if(valor == null){
                correrPrograma();
            }else{
                focusearLinea(Integer.valueOf(valor.get(1).toString()));
                colorearLinea(Integer.valueOf(valor.get(1).toString()), Color.RED);
                btnStop.setEnabled(false);
                mostrarDialogo("Error en el código", valor.get(0).toString());
            }
        } else {
            focusearLinea(Integer.valueOf(valor.get(1).toString()));
            colorearLinea(Integer.valueOf(valor.get(1).toString()), Color.RED);
            btnStop.setEnabled(false);
            btnStart.setEnabled(true);
            mostrarDialogo("Error en el código", valor.get(0).toString());
        }

        return 0;

    }
    public List<Object> verifySintaxis() {
        int indice = 0;
        while (indice < programaI.size()) {

            Object[] word = programaI.get(indice);

            if ("If".equals(word[0].toString().trim())) { // Verificar sentencia if
                if ("Not".equals(programaI.get(indice + 1)[0])) {
                    List<Object> result = verifyIf(programaI.subList(indice, indice + 5));
                    if (!(boolean) result.get(0)) {
                        return Arrays.asList(result.get(1));
                    } else {
                        indice += 4;
                    }
                } else {
                    List<Object> result = verifyIf(programaI.subList(indice, indice + 4));
                    if (!(boolean) result.get(0)) {
                        return Arrays.asList(result.get(1));
                    } else {
                        indice += 3;
                    }
                }
            } else if (actions.contains(word[0].toString().trim())) { // Verificar acciones
                List<Object> result = verifyAction(programaI.subList(indice, indice + 2));
                if (!(boolean) result.get(0)) {
                    return Arrays.asList("Error, instrucción incompleta en la línea: " + word[1] + ", " + word[0].toString().trim(), word[1]);
                } else {
                    indice += 1;
                }
            } else if (isCharacter(word[0].toString().trim()) || "Blank".equals(word[0].toString().trim())) { // Verificar carácter
                return Arrays.asList("Error, caracter \"" + word[0].toString().trim() + "\" en posición incorrecta, línea: " + word[1], word[1]);
            } else if (isFunction(word[0].toString().trim()) && isCallFunction(word[0].toString().trim())) { // Verificar si es un tag
                return Arrays.asList("Error, etiqueta \"" + word[0].toString().trim() + "\" en posición incorrecta, línea: " + word[1], word[1]);
            }

            if (isFunction(word[0].toString().trim()) && isFunction(programaI.get(indice + 1)[0].toString())) {
                if (!isCallFunction(word[0].toString().trim()) && !isCallFunction(programaI.get(indice + 1)[0].toString())) {
                    return Arrays.asList("Error, etiqueta vacía \"" + word[0].toString().trim() + "\" en la línea: " + word[1], word[1]);
                }
            }

            indice++;
        }

        return null; // No se encontraron errores de sintaxis
    }
    public void actualizarArregloWidget() {
        CustomDrawingView customView = findViewById(R.id.customDrawingView);
        customView.setArreglo(arregloPrincipal.toArray(new String[0])); // Convierte ArrayList a String[]
        customView.setPosicionArreglo(posicionArregloActual);

        int nuevoAncho = 10 + (arregloPrincipal.size() * 158);
        customView.setMinimumWidth(nuevoAncho);

        int nuevaPosicion = 10 + (posicionArregloActual * 158);
        HorizontalScrollView scrollArea = findViewById(R.id.horizontalScrollview);
        scrollArea.setScrollX(nuevaPosicion);
    }
    private void correrPrograma() {
        String entrada = inputEntrada.getText().toString();

        if (entrada.isEmpty()) {
            //labelErrores.setText("¡Ingresa al menos un valor en la entrada!"); // Verificación de que ingresen al menos uno
            return;
        }

        for(int i = 0; i < entrada.length(); i++){
            arregloPrincipal.add(String.valueOf(entrada.charAt(i)));
        }
        // Inicialización de las variables
        //labelErrores.setText("");
        palabraActual2 = programaI.get(0);
        posicionArregloActual = 0;
        actualizarArregloWidget();
        ultimoElemento = programaI.size() - 9;

        timer = new Timer();

        timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        animarPrograma();
                    }
                });
            }
        };

        inputAnterior = input.getText().toString();

        timer.schedule(timerTask, 0, 600);
    }
    private void animarPrograma() {
        if (Integer.valueOf(palabraActual2[2].toString().trim()) > ultimoElemento) {
            timer.cancel();
            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
            // En caso de que se llegue al final del programa
        }

        colorearLinea(Integer.valueOf(palabraActual2[1].toString()), Color.YELLOW); // Marcar la línea en la que va
        focusearLinea(Integer.valueOf(palabraActual2[1].toString())); // Marcar la línea en la que va

        int indice = Integer.valueOf(palabraActual2[2].toString()); // Obtener el índice de la palabra
        int indiceInicial = Integer.valueOf(palabraActual2[2].toString()); // Respaldar el índice

        Object[] word = palabraActual2; // Actualizar la variable word

        if (word[0].toString().trim().equals("If")) { // En caso de ser sentencia if
            if (programaI.get(indice + 1)[0].toString().trim().equals("Not")) {
                ejecutarIf(programaI.subList(indice, indice + 5));
                indice += 4; // Actualizar índice para saltar las palabras ya utilizadas
            } else {
                ejecutarIf(programaI.subList(indice, indice + 4));
                indice += 3; // Actualizar índice para saltar las palabras ya utilizadas
            }
        } else if (actions.contains(word[0].toString().trim())) { // En caso de que sea una acción
            ejecutarAction(programaI.subList(indice, indice + 2));
            indice += 1; // Actualizar índice para saltar las palabras ya utilizadas
        }

        indice += 1; // Mover al siguiente índice
        if (indiceInicial == Integer.valueOf(palabraActual2[2].toString().trim())) { // Actualizar la palabra actual
            palabraActual2 = programaI.get(indice);
        }

        contadorShot++;
        actualizarArregloWidget();
    }
    private void ejecutarIf(List<Object[]> words) {
        if (words.get(0)[0].toString().trim().equals("If")) {
            if (words.get(1)[0].toString().trim().equals("Not")) {
                if (words.get(2)[0].toString().trim().equals("Blank")) {
                    if (!arregloPrincipal.get(posicionArregloActual).equals(" ")) {
                        ejecutarAction(words.subList(3, 5));
                    }
                } else {
                    if (!arregloPrincipal.get(posicionArregloActual).equals(String.valueOf(words.get(2)[0].toString().trim().charAt(1)))) {
                        ejecutarAction(words.subList(3, 5));
                    }
                }
            } else {
                System.out.println("if without blank");
                if (words.get(1)[0].toString().trim().equals("Blank")) {
                    if (arregloPrincipal.get(posicionArregloActual).equals(" ")) {
                        ejecutarAction(words.subList(2, 4));
                    }
                } else {
                    if (arregloPrincipal.get(posicionArregloActual).equals(String.valueOf(words.get(1)[0].toString().trim().charAt(1)))) {
                        ejecutarAction(words.subList(2, 4));
                    }
                }
            }
        }
    }
    private void ejecutarAction(List<Object[]> words) {
        if (words.get(0)[0].toString().trim().equals("Goto")) { // Actualización de la palabra actual para hacer match con la etiqueta
            String palabra = words.get(1)[0].toString().trim() + ":";
            for (Object[] i : programaI) {
                if (i[0].toString().trim().equals(palabra)) {
                    palabraActual2 = i;
                }
            }
        } else if (words.get(0)[0].toString().trim().equals("Write")) { // Cambiar el valor de la cabeza
            String palabra;
            if (words.get(1)[0].toString().trim().equals("Blank")) {
                palabra = " ";
            } else {
                palabra = String.valueOf(words.get(1)[0].toString().trim().charAt(1));
            }
            arregloPrincipal.set(posicionArregloActual, palabra);
        } else if (words.get(0)[0].toString().trim().equals("Return")) { // Finalizar el programa con un return
            //returnPrograma = words.get(1)[0];
            timer.cancel();
            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
            //buttonContinue.setEnabled(false);
            if (words.get(1)[0].toString().trim().equals("True")) { // Dependiendo del true or false, pintar la línea de cierto color
                colorearLinea(Integer.valueOf(palabraActual2[1].toString()), Color.GREEN); // Marcar la línea en la que va
            } else {
                colorearLinea(Integer.valueOf(palabraActual2[1].toString()), Color.RED); // Marcar la línea en la que va
            }
        } else if (words.get(0)[0].toString().trim().equals("Move")) { // Mover la cabeza
            if (words.get(1)[0].toString().trim().equals("Right")) {
                if (posicionArregloActual == arregloPrincipal.size() - 1) {
                    arregloPrincipal.add(" ");
                }
                posicionArregloActual++;
            } else {
                if (posicionArregloActual == 0) {
                    arregloPrincipal.add(0, " ");
                } else {
                    posicionArregloActual--;
                }
            }
        }
    }
    private List<Object> verifyLanguage(List<ArrayList<String>> programa, List<String> reservedWords) {
        List<String> funciones = new ArrayList<>();
        if (!programa.get(0).get(0).equals("Start:")) {
            return Arrays.asList("Error en la línea 0: El programa debe iniciar con la etiqueta Start", 0);
        }

        for (int indexLine = 0; indexLine < programa.size(); indexLine++) {
            //Iterar en las lineas del programa
            ArrayList<String> line = programa.get(indexLine);
            for (String wordWithSpaces : line) {
                String word = wordWithSpaces.trim();
                if (reservedWords.contains(word)) {
                } else {
                    if (isCharacter(word)) {
                    } else {
                        if (!isFunction(word)) {
                            return Arrays.asList("Error en la línea " + (indexLine + 1) + ": Palabra desconocida, " + word, indexLine);
                        } else {
                            if (reservedWords.contains(word.substring(0, word.length() - 1))) {
                                return Arrays.asList("Error en la línea " + (indexLine + 1) + ": Las etiquetas no se pueden llamar como alguna palabra reservada, " + word, indexLine);
                            } else {
                                if (isCallFunction(word)) {
                                    boolean valor = false;
                                    for (ArrayList<String> line2 : programa) {
                                        for (String word2 : line2) {
                                            if ((word + ":").equals(word2)) {
                                                valor = true;
                                            }
                                        }
                                    }
                                    if (!valor) {
                                        return Arrays.asList("Error en la línea " + (indexLine + 1) + ": Etiqueta llamada sin declarar, " + word, indexLine);
                                    }
                                }
                            }
                        }
                    }
                }

                if (isFunction(word)) { // Verificar si la declaración de una etiqueta se repite
                    if (!isCallFunction(word)) {
                        funciones.add(word);
                        if (Collections.frequency(funciones, word) > 1) {
                            return Arrays.asList("Error, etiqueta repetida: " + word, indexLine);
                        }
                    }
                }
            }
        }
        return null; // No se encontraron errores
    }
    public List<Object> verifyIf(List<Object[]> words) {
        if ("If".equals(words.get(0)[0].toString().trim())) {
            if ("Not".equals(words.get(1)[0].toString().trim())) {
                String condition = words.get(2)[0].toString().trim();
                if ("Blank".equals(condition) || isCharacter(condition)) {
                    return verifyAction(words.subList(3, 5));
                } else {
                    return Arrays.asList(false, "Error, instrucción errónea " + condition + " en la línea: " + (Integer.parseInt(words.get(2)[1].toString()) + 1), words.get(2)[1].toString().trim());
                }
            } else {
                String condition = words.get(1)[0].toString().trim();
                if ("Blank".equals(condition) || isCharacter(condition)) {
                    return verifyAction(words.subList(2,4));
                } else {
                    return Arrays.asList(false, "Error, instrucción errónea " + condition + " en la línea: " + (Integer.parseInt(words.get(1)[1].toString()) + 1), words.get(1)[1].toString().trim());
                }
            }
        } else {
            return Arrays.asList(false, "Error, instrucción errónea " + words.get(0)[0].toString().trim() + " en la línea: " + (Integer.parseInt(words.get(0)[1].toString()) + 1), words.get(0)[1].toString().trim());
        }
    }

    public List<Object> verifyAction(List<Object[]> words) {
        String action = words.get(0)[0].toString().trim();



        if ("Goto".equals(action)) {
            return verifyGoto(words);
        } else if ("Write".equals(action)) {
            return verifyWrite(words);
        } else if ("Return".equals(action)) {
            return verifyReturn(words);
        } else if ("Move".equals(action)) {
            String moveDirection = words.get(1)[0].toString().trim();
            if ("Right".equals(moveDirection) || "Left".equals(moveDirection)) {
                return Arrays.asList(true, null);
            } else {
                return Arrays.asList(false, "Error, instrucción errónea " + moveDirection + " en la línea: " + (Integer.parseInt(words.get(1)[1].toString()) + 1), words.get(1)[1]);
            }
        } else {
            return Arrays.asList(false, "Error, instrucción errónea " + action + " en la línea: " + (Integer.parseInt(words.get(0)[1].toString()) + 1), words.get(0)[1]);
        }
    }

    public List<Object> verifyReturn(List<Object[]> words) {
        if ("True".equals(words.get(1)[0].toString().trim()) || "False".equals(words.get(1)[0].toString().trim())) {
            return Arrays.asList(true, null);
        } else {
            return Arrays.asList(false, "Error, instrucción errónea " + words.get(1)[0] + " en la línea: " + words.get(1)[1], words.get(1)[1]);
        }
    }

    public List<Object> verifyWrite(List<Object[]> words) {
        if ("Blank".equals(words.get(1)[0].toString().trim()) || isCharacter(words.get(1)[0].toString().trim())) {
            return Arrays.asList(true, null);
        } else {
            return Arrays.asList(false, "Error, instrucción errónea " + words.get(1)[0] + " en la línea: " + words.get(1)[1], words.get(1)[1]);
        }
    }

    public List<Object> verifyGoto(List<Object[]> words) {
        if (!reservedWords.contains(words.get(1)[0].toString().trim())) {
            return Arrays.asList(true, null);
        } else {
            return Arrays.asList(false, "Error, instrucción errónea " + words.get(1)[0] + " en la línea: " + (Integer.parseInt(words.get(1)[1].toString()) + 1), words.get(1)[1]);
        }
    }

    public boolean isCharacter(String word) { //Verificar si es un caracter
        if (word.length() == 3 && word.charAt(0) == '\'' && word.charAt(2) == '\'') {
            return true;
        } else {
            return false;
        }
    }

    public boolean isFunction(String word) {
        if(word.length() != 0){
            if (Character.isUpperCase(word.charAt(0))) {
                return true;
            } else {
                return false;
            }
        }else{
            return false;
        }
    }

    public boolean isCallFunction(String word) {
        if (word.charAt(word.length() - 1) != ':') {
            return true;
        } else {
            return false;
        }
    }
    private void mostrarDialogo(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null);
        builder.create().show();
    }

    private void abrirSelectorArchivos() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain"); // Filtra solo archivos .txt
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                leerArchivoTexto(uri);
            }
        } else if (requestCode == WRITE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                guardarArchivo(uri);
            }
        }
    }

    private void leerArchivoTexto(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            inputStream.close();
            input.setText(stringBuilder.toString()); // Asumiendo que 'input' es tu instancia de MyEditText
        } catch (IOException e) {
            // Manejo de excepciones
            e.printStackTrace();
        }
    }

    public void focusearLinea(int lineNumber) {
        int lineHeight = input.getLineHeight();
        int yPosition = lineHeight * lineNumber;

        // Desplaza el ScrollView
        scrollViewVertical.smoothScrollTo(0, yPosition);
    }
    public void colorearLinea(int lineNumber, int color) {
        SpannableString spannableString = new SpannableString(input.getText());

        // Remover todos los BackgroundColorSpan existentes
        BackgroundColorSpan[] spans = spannableString.getSpans(0, spannableString.length(), BackgroundColorSpan.class);
        for (BackgroundColorSpan span : spans) {
            spannableString.removeSpan(span);
        }

        Layout layout = input.getLayout();

        // Calcula el inicio y el final de la línea
        int start = layout.getLineStart(lineNumber);
        int end = layout.getLineEnd(lineNumber);

        // Aplica el nuevo color de fondo
        BackgroundColorSpan backgroundColorSpan = new BackgroundColorSpan(color);
        spannableString.setSpan(backgroundColorSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Actualiza el texto en el EditText
        input.setText(spannableString);
    }

    private void abrirDialogoGuardarArchivo() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TITLE, "archivo_exportado.txt");
        startActivityForResult(intent, WRITE_REQUEST_CODE);
    }

    private void guardarArchivo(Uri uri) {
        if (uri != null) {
            try {
                ParcelFileDescriptor pfd = getContentResolver().openFileDescriptor(uri, "w");
                FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
                String texto = input.getText().toString(); // Asumiendo que 'input' es tu instancia de EditText
                fileOutputStream.write(texto.getBytes());
                fileOutputStream.close();
                pfd.close();
                // Mostrar un Toast de éxito
                Toast.makeText(this, "Archivo guardado con éxito", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                // Manejo de excepciones, por ejemplo, mostrar un mensaje de error
                Toast.makeText(this, "Error al guardar el archivo: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            // Manejar el caso en que el URI es nulo (por ejemplo, mostrar un mensaje de error)
            Toast.makeText(this, "Error al guardar el archivo: URI no válido", Toast.LENGTH_SHORT).show();
        }
    }

}


