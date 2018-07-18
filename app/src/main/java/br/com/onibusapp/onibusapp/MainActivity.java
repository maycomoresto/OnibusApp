package br.com.onibusapp.onibusapp;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import br.com.onibusapp.onibusapp.ui.MapsFragment;
import br.com.onibusapp.onibusapp.ui.favoritos.FavoritosFragment;
import br.com.onibusapp.onibusapp.ui.pesquisa.PesquisarFragment;
import br.com.onibusapp.onibusapp.ui.pesquisa.PesquisarPresenter;
import br.com.onibusapp.onibusapp.utils.FragmentUtil;

public class MainActivity extends AppCompatActivity {

    private FragmentUtil fragmentUtil;
    private PesquisarPresenter pesquisarPresenter;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragmentUtil.mudarTela(new MapsFragment());
                    return true;
                case R.id.navigation_dashboard:
                    PesquisarFragment pesquisarFragment = new PesquisarFragment();
                    pesquisarPresenter = new PesquisarPresenter(pesquisarFragment);
                    fragmentUtil.mudarTela(pesquisarFragment);
                    return true;
                case R.id.navigation_notifications:
                    fragmentUtil.mudarTela(new FavoritosFragment());
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        this.fragmentUtil = FragmentUtil.getInstance(getSupportFragmentManager());
        inicializaTelaMapa();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void inicializaTelaMapa() {
        fragmentUtil.mudarTela(new MapsFragment());
    }

}
