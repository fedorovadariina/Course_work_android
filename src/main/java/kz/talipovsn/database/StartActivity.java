package kz.talipovsn.database;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class StartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }
    public void onExit(View v) {
        finishAffinity();
    }
    // МЕТОД ДЛЯ КНОПКИ "Далее"
    public void onNext(View v) {
        // Создание второго окна
        Intent intent = new Intent(StartActivity.this, MainActivity.class);
        // Запуск второго окна
        startActivity(intent);
    }
}
