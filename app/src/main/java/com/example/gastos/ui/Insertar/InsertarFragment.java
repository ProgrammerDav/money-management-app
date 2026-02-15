package com.example.gastos.ui.Insertar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.gastos.DB;
import com.example.gastos.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class InsertarFragment extends Fragment {

    EditText concepto, tienda, cantidad;
    Spinner persona;
    Button botonEnviar, botonModificar;
    LinearLayout layoutPrincipal;

    DB db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_insertar, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        concepto = view.findViewById(R.id.concepto);
        tienda = view.findViewById(R.id.tienda);
        cantidad = view.findViewById(R.id.cantidad);
        persona = view.findViewById(R.id.Persona);
        botonEnviar = view.findViewById(R.id.botonEnviar);
        botonModificar = view.findViewById(R.id.botonModificar);
        layoutPrincipal = view.findViewById(R.id.LayoutPrincipal);

        db = new DB(requireContext());

        cargarSpinner();

        botonEnviar.setOnClickListener(v -> {

            if (persona.getSelectedItem() == null) {
                Toast.makeText(requireContext(), "No hay personas disponibles", Toast.LENGTH_SHORT).show();
                return;
            }

            String concep = concepto.getText().toString().trim();
            String tien = tienda.getText().toString().trim();
            String perso = persona.getSelectedItem().toString().trim();
            String canti = cantidad.getText().toString().trim();

            if (concep.isEmpty() || tien.isEmpty() || canti.isEmpty()) {
                Toast.makeText(requireContext(), "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            double canti2 = Double.parseDouble(canti);

            String fecha = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    .format(new Date());

            boolean insertado = db.registrar(concep, tien, canti2, fecha, perso);

            if (insertado) {
                Toast.makeText(requireContext(), "Gasto insertado", Toast.LENGTH_SHORT).show();
            }
        });

        botonModificar.setOnClickListener(v -> {

            layoutPrincipal.removeAllViews();

            EditText nombre1 = new EditText(requireContext());
            nombre1.setHint("Nuevo nombre 1");

            EditText nombre2 = new EditText(requireContext());
            nombre2.setHint("Nuevo nombre 2");

            Button guardar = new Button(requireContext());
            guardar.setText("Guardar nombres");

            layoutPrincipal.addView(nombre1);
            layoutPrincipal.addView(nombre2);
            layoutPrincipal.addView(guardar);

            guardar.setOnClickListener(v1 -> {

                String nuevo1 = nombre1.getText().toString().trim();
                String nuevo2 = nombre2.getText().toString().trim();

                if (nuevo1.isEmpty() || nuevo2.isEmpty()) {
                    Toast.makeText(requireContext(), "Completa los nombres", Toast.LENGTH_SHORT).show();
                    return;
                }

                db.modificarPersonas(nuevo1, nuevo2);
                cargarSpinner();

                Toast.makeText(requireContext(), "Nombres actualizados", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void cargarSpinner() {
        ArrayList<String> personas = db.obtenerPersonas();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                personas
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        persona.setAdapter(adapter);
    }
}