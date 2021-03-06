package ru.sibhtc.educationdemo.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import ru.sibhtc.educationdemo.R;
import ru.sibhtc.educationdemo.models.Student;
import ru.sibhtc.educationdemo.mock.StudentMock;

/**
 * Created by Антон on 17.09.2015.
 **/
public class DetailsFragment extends Fragment {
    /**
     * Create a new instance of DetailsFragment, initialized to
     * show the text at 'index'.
     */
    public static DetailsFragment newInstance(int index) {
        DetailsFragment f = new DetailsFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);

        return f;
    }

    public int getShownIndex() {
        return getArguments().getInt("index", 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        View view = inflater.inflate(R.layout.details_fragment, container, false);


        TextView nameText       = (TextView)view.findViewById(R.id.nameText);
        TextView surnameText    = (TextView)view.findViewById(R.id.surnameText);
        TextView patrText       = (TextView)view.findViewById(R.id.patrText);
        TextView qualText       = (TextView)view.findViewById(R.id.qualText);
        TextView ageText        = (TextView)view.findViewById(R.id.ageText);

        ArrayList<Student> students = StudentMock.getStudentList();
        nameText.setText(students.get(getShownIndex()).Name);
        surnameText.setText(students.get(getShownIndex()).Surname);
        patrText.setText(students.get(getShownIndex()).Patronimity);
        qualText.setText(students.get(getShownIndex()).Qualification);
        ageText.setText(Integer.toString(students.get(getShownIndex()).Age));
        return view;
    }
}
