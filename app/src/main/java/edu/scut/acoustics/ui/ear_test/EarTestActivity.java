package edu.scut.acoustics.ui.ear_test;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import java.util.Objects;

import edu.scut.acoustics.R;
import edu.scut.acoustics.databinding.ActivityEarTestBinding;

public class EarTestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        ActivityEarTestBinding earTestBinding = DataBindingUtil.setContentView(this, R.layout.activity_ear_test);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(R.id.left_ear, R.id.right_ear).build();
        NavController navController = Navigation.findNavController(this, R.id.fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(earTestBinding.navigation, navController);
    }
}
