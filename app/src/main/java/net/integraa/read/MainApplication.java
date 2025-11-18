package net.integraa.read;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.os.storage.OnObbStateChangeListener;
import android.os.storage.StorageManager;
import android.util.Log;

import net.integraa.read.controller.Scanner;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dalvik.system.DexClassLoader;

public class MainApplication extends Application {
    private static final String TAG = "OBB_LOADER";
    // Sostituisci con il nome corretto del tuo OBB (deve corrispondere al tuo build.gradle)
    private static final String OBB_FILE_NAME = "main.23.net.integraa.read.obb";
    // Il nome della sottocartella che hai definito nel task Gradle Zip (into 'libs')
    private static final String LIBS_SUBDIR = "/libs";

    private static DexClassLoader obbClassLoader;
    private static OBBStateListener obbStateListener;

    @Override
    public void onCreate() {
        super.onCreate();
        Scanner.initialize(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // Avvia il processo di montaggio
    }


    public static void mountObbFile(Context context) {
        //String obbFilePath = Environment.getExternalStorageDirectory() + "/Android/obb/" +
        File obbFile = new File(context.getObbDir(),OBB_FILE_NAME);

        if (!obbFile.exists()) {
            Log.e(TAG, "File OBB non trovato. L'app fallirà.");
            return;
        }

        if (!obbFile.canRead()) {
            Log.e(TAG, "File OBB non canRead. L'app fallirà.");
            return;
        }

        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);

        // Se è già montato, carichiamo immediatamente.
        if (storageManager.isObbMounted(obbFile.getAbsolutePath())) {
            Log.w(TAG, "OBB già montato.");
            injectObbToClassLoader(context, storageManager.getMountedObbPath(obbFile.getAbsolutePath()));
            return;
        }

        // Avvia il montaggio asincrono, ma dobbiamo bloccare il thread qui
        // per assicurarci che sia montato PRIMA che onCreate() venga chiamato

        // ... La gestione del montaggio asincrono è complessa in attachBaseContext.
        // La soluzione più robusta è *forzare* il montaggio sincrono o usare un pattern di blocco.

        // Per semplicità e robustezza: Eseguire la logica di montaggio e iniezione nel listener
        // e bloccare l'esecuzione della prima Activity fino a quando non è completo.
        // Usiamo un listener leggermente modificato:
        obbStateListener = new OBBStateListener(context);
        storageManager.mountObb(obbFile.getAbsolutePath(), null, obbStateListener);
    }

    // 3. Metodo per iniettare i JAR nel PathClassLoader esistente
    static void injectObbToClassLoader(Context context, String mountedPath) {
        try {
            // Otteniamo il PathClassLoader dell'applicazione
            ClassLoader pathClassLoader = (ClassLoader) context.getClassLoader();

            // La directory dove sono i nostri JAR nell'OBB
            File obbJarDir = new File(mountedPath + LIBS_SUBDIR);

            if (!obbJarDir.exists() || obbJarDir.listFiles() == null) {
                Log.e(TAG, "Directory JAR nell'OBB non trovata o vuota!");
                return;
            }

            // ----------------------------------------------------
            // Logica di iniezione (simile a MultiDex)
            // ----------------------------------------------------

            // 1. Ottenere il campo 'pathList' dal ClassLoader (Reflection)
            Field pathListField = findField(pathClassLoader, "pathList");
            Object pathList = pathListField.get(pathClassLoader);

            // 2. Ottenere l'array di elementi 'dexElements' da pathList (Reflection)
            Field dexElementsField = findField(pathList, "dexElements");
            Object[] existingElements = (Object[]) dexElementsField.get(pathList);

            // 3. Creare nuovi elementi per i JAR dell'OBB
            // Questo è un metodo interno di Android (Reflection)
            java.lang.reflect.Method makeDexElementsMethod = findMethod(
                    pathList, "makePathElements", List.class, File.class, List.class
            );

            List<File> obbJars = new ArrayList<>();
            // Aggiungi tutti i JAR dalla sottodirectory 'libs'
            for (File file : obbJarDir.listFiles()) {
                if (file.getName().endsWith(".jar")) {
                    obbJars.add(file);
                }
            }

            // Chiama makePathElements per creare i nuovi oggetti Element
            Object[] newElements = (Object[]) makeDexElementsMethod.invoke(
                    pathList, obbJars, obbJarDir, new ArrayList<IOException>()
            );

            // 4. Combinare i vecchi elementi con i nuovi
            Object[] combined = (Object[]) Array.newInstance(
                    existingElements.getClass().getComponentType(),
                    existingElements.length + newElements.length
            );

            // Metti i nuovi elementi (OBB) all'inizio, così hanno la precedenza
            System.arraycopy(newElements, 0, combined, 0, newElements.length);
            System.arraycopy(existingElements, 0, combined, newElements.length, existingElements.length);

            // 5. Iniettare l'array combinato nel PathClassLoader
            dexElementsField.set(pathList, combined);

            Log.i(TAG, "Librerie OBB iniettate con successo nel PathClassLoader. Ora puoi usare gli import.");

        } catch (Exception e) {
            Log.e(TAG, "Errore fatale nell'iniezione del ClassLoader (Reflection):", e);
        }
    }

    /**
     * Trova un campo specifico (Field) in una classe, risalendo la gerarchia delle superclassi.
     * @param instance L'oggetto in cui cercare il campo.
     * @param name Il nome del campo da trovare (es. "pathList").
     * @return Il campo trovato.
     * @throws NoSuchFieldException Se il campo non viene trovato.
     */
    private static Field findField(Object instance, String name) throws NoSuchFieldException {
        // Itera sulla gerarchia delle classi (dalla più specifica alla più generica)
        for (Class<?> clazz = instance.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            try {
                // Tenta di ottenere il campo
                Field field = clazz.getDeclaredField(name);

                // Rende il campo accessibile anche se è privato (cruciale)
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }

                return field;
            } catch (NoSuchFieldException e) {
                // Continua la ricerca nella superclasse
            }
        }
        throw new NoSuchFieldException("Field " + name + " non trovato in " + instance.getClass());
    }

    /**
     * Trova un metodo specifico in una classe, risalendo la gerarchia delle superclassi.
     * @param instance L'oggetto in cui cercare il metodo.
     * @param name Il nome del metodo da trovare (es. "makePathElements").
     * @param parameterTypes I tipi dei parametri del metodo.
     * @return Il metodo trovato.
     * @throws NoSuchMethodException Se il metodo non viene trovato.
     */
    private static Method findMethod(Object instance, String name, Class<?>... parameterTypes)
            throws NoSuchMethodException {

        // Itera sulla gerarchia delle classi
        for (Class<?> clazz = instance.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            try {
                // Tenta di ottenere il metodo
                Method method = clazz.getDeclaredMethod(name, parameterTypes);

                // Rende il metodo accessibile anche se è privato (cruciale)
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }

                return method;
            } catch (NoSuchMethodException e) {
                // Continua la ricerca nella superclasse
            }
        }
        throw new NoSuchMethodException("Method " + name + " con parametri " + Arrays.toString(parameterTypes) +
                " non trovato in " + instance.getClass());
    }

    // --- Listener Asincrono ---
}

class OBBStateListener extends OnObbStateChangeListener {
    public final Context context;

    public OBBStateListener(Context context) { this.context = context; }

    @Override
    public void onObbStateChange(String path, int state) {
        super.onObbStateChange(path, state);
        if (state == MOUNTED) {
            StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
            String mountedPath = storageManager.getMountedObbPath(path);

            // Chiama il metodo per l'iniezione
            MainApplication.injectObbToClassLoader(context,mountedPath);
        }
        // ... Gestione UNMOUNTED/Errori come prima ...
    }
}

