
package org.ranobe.ranobe.ui.reader;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.ranobe.ranobe.R;
import org.ranobe.ranobe.database.MLSettings;
import org.ranobe.ranobe.models.TranslationViewModel;

import java.util.ArrayList;
import java.util.List;

public class TranslationFragment extends Fragment {

    private TranslationViewModel viewModel;
    private Spinner sourceLanguageSpinner;
    private Spinner targetLanguageSpinner;
    private TextView originalTextView;
    private Button translateButton;
    private ProgressBar loadingProgressBar;
    private TextView translatedTextView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.translation_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(TranslationViewModel.class);

        sourceLanguageSpinner = view.findViewById(R.id.sourceLanguageSpinner);
        targetLanguageSpinner = view.findViewById(R.id.targetLanguageSpinner);
        originalTextView = view.findViewById(R.id.originalTextView);
        translateButton = view.findViewById(R.id.translateButton);
        loadingProgressBar = view.findViewById(R.id.loadingProgressBar);
        translatedTextView = view.findViewById(R.id.translatedTextView);

        List<String> languages = new ArrayList<>();
        for (String code : MLSettings.getSupportedLanguages()) {
            languages.add(MLSettings.fromShortToDisplay(code));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, languages);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sourceLanguageSpinner.setAdapter(adapter);
        targetLanguageSpinner.setAdapter(adapter);

        translateButton.setOnClickListener(v -> {
            String text = originalTextView.getText().toString();
            int sourcePos = sourceLanguageSpinner.getSelectedItemPosition();
            int targetPos = targetLanguageSpinner.getSelectedItemPosition();
            String fromLang = (new ArrayList<>(MLSettings.getSupportedLanguages())).get(sourcePos);
            String toLang = (new ArrayList<>(MLSettings.getSupportedLanguages())).get(targetPos);

            viewModel.initTranslation(fromLang, toLang);
            viewModel.translateText(text);
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                loadingProgressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        viewModel.getTranslatedText().observe(getViewLifecycleOwner(), text -> {
            translatedTextView.setText(text);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                // Mostrar error al usuario (puedes usar Toast o Snackbar)
            }
        });
    }
}
