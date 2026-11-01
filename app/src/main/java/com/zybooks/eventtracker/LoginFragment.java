package com.zybooks.eventtracker;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.zybooks.eventtracker.model.User;
import com.zybooks.eventtracker.viewmodel.UserViewModel;

public class LoginFragment extends Fragment {
    private UserViewModel mUserViewModel;
    private User mUser;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        LoginFragment fragment = new LoginFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        Toast userExistsToast = Toast.makeText(
            rootView.getContext(),
            getString(R.string.toast_username_exists),
            Toast.LENGTH_SHORT);

        Toast invalidUserToast = Toast.makeText(
            rootView.getContext(),
            getString(R.string.toast_invalid_login),
            Toast.LENGTH_SHORT);

        // get the ViewModel
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        // add click callback to 'save user' button
        Button saveUserButton =
                (Button)rootView.findViewById(R.id.create_account_button);

        saveUserButton.setOnClickListener(buttonView -> {
            // set username
            EditText usernameEditText = rootView.findViewById(R.id.input_username);
            String username = usernameEditText.getText().toString();

            // set password
            EditText passwordEditText = rootView.findViewById(R.id.input_password);
            String password = passwordEditText.getText().toString();

            // if user does not exist, create one, otherwise notify user it already exists
            User user = mUserViewModel.findUserByUsername(username);

            // TODO: the user should be set on the event list and
            //  the rest of the app so it knows whether the user can access it
            //  the login handler should behave this way as well

            // if no such user exists, add it
            if (user == null) {
                user = mUserViewModel.addUser(username, password);
            }
            else {
                userExistsToast.show();
                return;
            }

            // if the created user is valid, proceed to event list
            if (user != null) {
                Navigation.findNavController(buttonView).navigate(R.id.navigation_event_list);
            }
            else {
                invalidUserToast.show();
            }
        });

        // add click callback to 'login' button
        Button loginButton =
                (Button)rootView.findViewById(R.id.login_button);

        loginButton.setOnClickListener(buttonView -> {
            // set username
            EditText usernameEditText = rootView.findViewById(R.id.input_username);
            String username = usernameEditText.getText().toString();

            // set password
            EditText passwordEditText = rootView.findViewById(R.id.input_password);
            String password = passwordEditText.getText().toString();

            // check if user exists
            User user = mUserViewModel.findUser(username, password);

            // TODO: the user should be set on the event list and
            //  the rest of the app so it knows whether the user can access it
            //  the create account handler should behave this way as well

            // if user exists, login; otherwise, cancel
            if (user != null) {
                user.loggedIn = true;
                user = mUserViewModel.updateUser(user);
            }
            else {
                invalidUserToast.show();
                return;
            }

            // // if the logged in user is valid, proceed to event list
            if (user != null) {
                Navigation.findNavController(buttonView).navigate(R.id.navigation_event_list);
            }
            else {
                invalidUserToast.show();
            }
        });

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getParentFragmentManager()
            .beginTransaction().remove((Fragment)this).commitAllowingStateLoss();
    }
}