package com.example.gastos.ui.Principal;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.gastos.R;
import com.example.gastos.DB;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;

public class PrincipalFragment extends Fragment {

    public PrincipalFragment() {
        super(R.layout.fragment_principal);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        BarChart barChart = view.findViewById(R.id.barChart);
        DB db = new DB(requireContext());

        ArrayList<String> personas = db.obtenerPersonas();

        if (personas.isEmpty()) {
            barChart.clear();
            return;
        }

        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0; i < personas.size(); i++) {
            float total = (float) db.totalPorPersona(personas.get(i));
            entries.add(new BarEntry(i, total));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Gastos por persona");

        boolean isDarkMode = (getResources().getConfiguration().uiMode
                & android.content.res.Configuration.UI_MODE_NIGHT_MASK)
                == android.content.res.Configuration.UI_MODE_NIGHT_YES;

        if (isDarkMode) {
            dataSet.setColor(getResources().getColor(R.color.barra_noche));
            dataSet.setValueTextColor(getResources().getColor(R.color.barra_textoNoche));
        } else {
            dataSet.setColor(getResources().getColor(R.color.barra_principal));
            dataSet.setValueTextColor(getResources().getColor(R.color.barra_texto));
        }

        dataSet.setValueTextSize(14f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.5f);

        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);

        // Configurar eje X
        String[] nombres = personas.toArray(new String[0]);
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(nombres));
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(isDarkMode ?
                getResources().getColor(R.color.barra_textoNoche) :
                getResources().getColor(R.color.barra_texto));

        // Configurar eje izquierdo
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setTextColor(isDarkMode ?
                getResources().getColor(R.color.barra_textoNoche) :
                getResources().getColor(R.color.barra_texto));

        // Desactivar eje derecho
        barChart.getAxisRight().setEnabled(false);

        // Cambiar color de la leyenda ("Gastos por persona")
        barChart.getLegend().setTextColor(isDarkMode ?
                getResources().getColor(R.color.barra_textoNoche) :
                getResources().getColor(R.color.barra_texto));

        barChart.animateY(1000);
        barChart.invalidate();
    }
}
