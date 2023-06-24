package kz.talipovsn.database;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.AdapterView;
import android.widget.EditText;

import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


    private final int SMALL_FONT = 12; // Размер шрифта для режима обычного шрифта
    private int fontSize = SMALL_FONT; // Выбранный размер шрифта

    MySQLite db = new MySQLite(this); // Класс работы с нашей базой данных

    EditText editText; // Компонент для задания строки поиска
    TextView textView; // Компонент для вывода ответа
    SwitchCompat switch1;
    Spinner spinner;


    static final String FILTER = "FILTER"; // Имя параметра для сохранения при переворачивании экрана
    String filter = "";
    String filter1 = "";

    SharedPreferences sPref; // Класс для работы с настройками программы
    static final String CONFIG_FILE_NAME = "Config"; // Имя файла настроек приложения
    static final String FONT_SIZE = "FontSize"; // Имя параметра для сохранения размера шрифта в настройках приложения

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Сохранение данных при перевороте экрана
        savedInstanceState.putString(FILTER, filter);

        super.onSaveInstanceState(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Активация меню
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Доступ к компонентам
        editText = findViewById(R.id.editText);
        textView = findViewById(R.id.textView);
        switch1 = findViewById(R.id.switch1);
        spinner = findViewById(R.id.spinner);
        textView.setKeyListener(null);
        sPref = getSharedPreferences(CONFIG_FILE_NAME, MODE_PRIVATE);
        fontSize = sPref.getInt(FONT_SIZE, SMALL_FONT);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize); // Установка начально размера шрифта
        textView.requestFocus(); // Передача фокуса на комонент чтобы закрылось окно ввода у "editText"
        // Восстановление фильтра после переворота экрана
        if (savedInstanceState != null) {
            editText.setText(savedInstanceState.getString(FILTER));
        }
        switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean checked = ((SwitchCompat) v).isChecked();
                if (checked){
                    spinner.setVisibility(View.VISIBLE);
                }
                else{
                    spinner.setVisibility(View.GONE);
                    editText.setVisibility(View.VISIBLE);
                }
            }
        });
        textView.setText(R.string.Загрузка_данных);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                new Thread(new Runnable() {
                    public void run() {
                        filter1 = Integer.toString(selectedItemPosition);
                        final String data1 = db.getData(filter,filter1);
                        // Сделаем вывод результата синхронно с основным потоком
                        textView.post(new Runnable() {
                            public void run() {
                                textView.setText(data1);
                            }
                        });
                    }
                }).start();
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        // Обработчик изменения текста в компоненте "editText"
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                // Сделаем поиск данных в другом потоке
                new Thread(new Runnable() {
                    public void run() {
                        filter = editText.getText().toString().trim();
                        final String data = db.getData(filter,filter1);
                        // Сделаем вывод результата синхронно с основным потоком
                        textView.post(new Runnable() {
                            public void run() {
                                textView.setText(data);
                            }
                        });
                    }
                }).start();
            }
        });
        // Инициализация начального поиска (показать все записи)
        editText.post(new Runnable() {
            public void run() {
                editText.setText(filter);
            }
        });
    }



    public void onExit(View v) {
        finishAffinity();
    }
    public void onBack(View v) {
        // Создание первого окна
        Intent intent = new Intent(MainActivity.this, StartActivity.class);
        // Запуск первого окна
        startActivity(intent);
    }
    // Метод при закрытии окна
    @Override
    protected void onStop() {
        super.onStop();
        // Сохранение размера шрифта в настройках программы
        SharedPreferences.Editor ed = sPref.edit();
        ed.putInt(FONT_SIZE, fontSize);
        ed.apply();
    }
}
