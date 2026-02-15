package com.example.gastos.ui.Datos;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.gastos.DB;
import com.example.gastos.R;
import com.example.gastos.databinding.FragmentDatosBinding;

import java.util.ArrayList;
import java.util.HashMap;

public class DatosFragment extends Fragment {

    LinearLayout contenedor;
    Button btnReset;
    private FragmentDatosBinding binding;

    public DatosFragment() {
        super(R.layout.fragment_datos);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding = FragmentDatosBinding.bind(view);

        contenedor = binding.contenerdor;
        btnReset = binding.btnReset;

        cargarDatos();

        btnReset.setOnClickListener(v -> {
            DB db = new DB(requireContext());
            db.resetDatos();

            contenedor.removeAllViews();
            cargarDatos(); // vuelve a cargar vacío
        });
    }

    private void cargarDatos() {

        contenedor.removeAllViews();

        float scale = getResources().getDisplayMetrics().density;
        DB db = new DB(requireContext());

        // 🔥 Ahora usamos la tabla personas
        ArrayList<String> personas = db.obtenerPersonas();

        for (String persona : personas) {

            LinearLayout grupo = new LinearLayout(requireContext());
            grupo.setOrientation(LinearLayout.VERTICAL);

            LinearLayout.LayoutParams paramsGrupo = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            grupo.setLayoutParams(paramsGrupo);

            // Botón persona
            Button btnPersona = new Button(requireContext());
            btnPersona.setText(persona);
            btnPersona.setAllCaps(false);
            btnPersona.setTextColor(getResources().getColor(R.color.white));
            btnPersona.setBackgroundResource(R.drawable.botoncustom);
            btnPersona.setTextSize(16);

            int marginInPx = (int) (10 * scale + 0.5f);
            LinearLayout.LayoutParams paramsBtn = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            paramsBtn.setMargins(0, marginInPx, 0, 0);
            btnPersona.setLayoutParams(paramsBtn);

            // Layout oculto detalles
            LinearLayout detallesGasto = new LinearLayout(requireContext());
            detallesGasto.setOrientation(LinearLayout.VERTICAL);
            detallesGasto.setVisibility(View.GONE);

            grupo.addView(btnPersona);
            grupo.addView(detallesGasto);
            contenedor.addView(grupo);

            btnPersona.setOnClickListener(v -> {

                if (detallesGasto.getVisibility() == View.GONE) {

                    detallesGasto.removeAllViews();

                    ArrayList<HashMap<String, String>> infoGastos =
                            db.infoGastos(persona);

                    if (infoGastos.isEmpty()) {
                        TextView vacio = new TextView(requireContext());
                        vacio.setText("No hay gastos");
                        vacio.setPadding(50, 10, 0, 10);
                        detallesGasto.addView(vacio);
                    }

                    for (HashMap<String, String> gasto : infoGastos) {

                        StringBuilder sb = new StringBuilder();

                        if (gasto.get("concepto") != null)
                            sb.append("Concepto: ").append(gasto.get("concepto")).append("\n");

                        if (gasto.get("tienda") != null)
                            sb.append("Tienda: ").append(gasto.get("tienda")).append("\n");

                        if (gasto.get("cantidad") != null)
                            sb.append("Cantidad: ").append(gasto.get("cantidad")).append("€\n");

                        if (gasto.get("fecha") != null)
                            sb.append("Fecha: ").append(gasto.get("fecha")).append("\n");

                        TextView textView = new TextView(requireContext());
                        textView.setText(sb.toString().trim());
                        textView.setTextSize(16);
                        textView.setPadding(50, 5, 0, 5);

                        detallesGasto.addView(textView);
                    }

                    detallesGasto.setVisibility(View.VISIBLE);

                } else {
                    detallesGasto.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
