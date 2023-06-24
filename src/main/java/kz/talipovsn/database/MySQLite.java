package kz.talipovsn.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.StringTokenizer;

public class MySQLite extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 5; // НОМЕР ВЕРСИИ БАЗЫ ДАННЫХ И ТАБЛИЦ !
    static final String DATABASE_NAME = "phones"; // Имя базы данных
    static final String TABLE_NAME = "emergency_service"; // Имя таблицы
    static final String ID = "id"; // Поле с ID
    static final String NAME = "name"; // Поле с названием
    static final String NAME_LC = "name_lc"; // // Поле с названием в нижнем регистре
    static final String URL = "url"; // Поле с ссылкой
    static final String PRICE = "price"; // Поле с ценой
    static final String ASSETS_FILE_NAME = "washing_machines.txt"; // Имя файла из ресурсов с данными для БД
    static final String DATA_SEPARATOR = "|"; // Разделитель данных в файле ресурсов
    private final Context context; // Контекст приложения
    public MySQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }
    // Метод создания базы данных и таблиц в ней
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_NAME + "("
                + ID + " INTEGER PRIMARY KEY,"
                + NAME + " TEXT,"
                + NAME_LC + " TEXT,"
                + PRICE + " INTEGER,"
                + URL  + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
        System.out.println(CREATE_CONTACTS_TABLE);
        loadDataFromAsset(context, ASSETS_FILE_NAME,  db);
    }
    // Метод при обновлении структуры базы данных и/или таблиц в ней
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        System.out.println("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    // Добавление
    public void addData(SQLiteDatabase db, String name, String url, String price) {
        ContentValues values = new ContentValues();
        values.put(NAME, name);
        values.put(NAME_LC, name.toLowerCase());
        values.put(PRICE, price);
        values.put(URL, url);
        db.insert(TABLE_NAME, null, values);
    }
    // Добавление записей в базу данных из файла ресурсов
    public void loadDataFromAsset(Context context, String fileName, SQLiteDatabase db) {
        BufferedReader in = null;
        try {
            // Открываем поток для работы с файлом с исходными данными
            InputStream is = context.getAssets().open(fileName);
            // Открываем буфер обмена для потока работы с файлом с исходными данными
            in = new BufferedReader(new InputStreamReader(is));
            String str;
            while ((str = in.readLine()) != null) { // Читаем строку из файла
                String strTrim = str.trim(); // Убираем у строки пробелы с концов
                if (!strTrim.equals("")) { // Если строка не пустая, то
                    StringTokenizer st = new StringTokenizer(strTrim, DATA_SEPARATOR); // Нарезаем ее на части
                    String name = st.nextToken().trim();
                    String price = st.nextToken().trim();// Извлекаем из строки цену
                    String url = st.nextToken().trim(); // Извлекаем из строки ссылку
                    addData(db, name, url, price);
                }}
            // Обработчики ошибок
        } catch (IOException ignored) {
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {
                }}}}
    // Получение значений данных из БД в виде строки с фильтром
    public String getData(String filter,String filter1 ) {
        String selectQuery; // Переменная для SQL-запроса
            if (filter.equals("")) {
                    selectQuery = "SELECT  * FROM " + TABLE_NAME + " ORDER BY " + NAME;
                }
                else {
                    selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE (" + NAME_LC + " LIKE '%" +
                            filter.toLowerCase() + "%'" +
                            " OR " + URL + " LIKE '%" + filter + "%'" + ") ORDER BY " + NAME;
                }
            switch (filter1){
                case ("0") :
                    selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE " + PRICE + " BETWEEN 0 AND 100000" + " ORDER BY " + NAME;
                    break;
                case ("1") :
                    selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE " + PRICE + " BETWEEN 100000 AND 200000"+"  ORDER BY " + NAME;
                    break;
                case ("2") :
                    selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE " + PRICE + " BETWEEN 200000 AND 300000"+"  ORDER BY " + NAME;
                    break;
                case ("3") :
                    selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE " + PRICE + " BETWEEN 300000 AND 400000"+"  ORDER BY " + NAME;
                    break;
                case ("4") :
                    selectQuery = "SELECT  * FROM " + TABLE_NAME + " WHERE " + PRICE + " >="+"400000" + " ORDER BY " + NAME;
                    break;
                default:
                    break;
            }
        SQLiteDatabase db = this.getReadableDatabase(); // Доступ к БД
        Cursor cursor = db.rawQuery(selectQuery, null); // Выполнение SQL-запроса
        StringBuilder data = new StringBuilder(); // Переменная для формирования данных из запроса
        int num = 0;
        if (cursor.moveToFirst()) { // Если есть хоть одна запись, то
            do { // Цикл по всем записям результата запроса
                int n = cursor.getColumnIndex(NAME);
                int t = cursor.getColumnIndex(URL);
                int p = cursor.getColumnIndex(PRICE);
                String name = cursor.getString(n); // Чтение названия
                String url = cursor.getString(t);
                int price = cursor.getInt(p);// Чтение цены
                data.append(String.valueOf(++num) + ") " + name + ": " + price +" " + url+ "\n");
            } while (cursor.moveToNext()); // Цикл пока есть следующая запись
        }
        return data.toString(); // Возвращение результата
    }
    }

