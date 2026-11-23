package com.zybooks.eventtracker;

//import static com.zybooks.eventtracker.backgroundservice.EncryptedDataStore.PREF_FILE_NAME;

import android.content.Context;
//import android.content.Intent;
import android.content.SharedPreferences;
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

import com.zybooks.eventtracker.apiservice.RemoteRepository;
//import com.zybooks.eventtracker.dbmodel.User;
import com.zybooks.eventtracker.clientmodel.User;
import com.zybooks.eventtracker.viewmodel.UserViewModel;
import com.zybooks.eventtracker.viewmodel.UserViewModelFactory;

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

    private Toast createToast (View rootView, String msg) {
        return Toast.makeText(
                rootView.getContext(),
                msg,
                Toast.LENGTH_SHORT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        // load the viewmodel with the API dependency
        final String PREF_FILE_NAME = getString(R.string.pref_file_name);
        SharedPreferences sharedPreferences =
                requireContext()
                        .getApplicationContext()
                        .getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        RemoteRepository api = new RemoteRepository(sharedPreferences);
        UserViewModelFactory factory =
                new UserViewModelFactory(requireActivity().getApplication(), api);
        mUserViewModel = new ViewModelProvider(this, factory).get(UserViewModel.class);

        // TODO: replace with online-first logic:
        //       instead of looking up username and creating if missing,
        //       create the request (LoginRequest.username = username, LoginRequest.password = password, error msg = msg)
        //       call the api register method
        //       if errors, toast and break
        //       get UserResponse
        //       stringify UserResponse (if not combined into one step)
        //
        // TODO: the user should be set on the event list and
        //  the rest of the app so it knows whether the user can access it
        //  the login handler should behave this way as well
        // add click callback to 'save user' button
        Button saveUserButton =
                (Button)rootView.findViewById(R.id.create_account_button);

        saveUserButton.setOnClickListener(buttonView -> {
            // get username string from UI username field
            EditText usernameEditText = rootView.findViewById(R.id.input_username);
            String username = usernameEditText.getText().toString();

            // get password string from UI password field
            EditText passwordEditText = rootView.findViewById(R.id.input_password);
            String password = passwordEditText.getText().toString();

            // TODO: attempt registration
            // TODO: check if user exists
            //User user = mUserViewModel.findUser(username, password);
            User newUser = new User();
            newUser.username = username;
            newUser.password = password;

            // register user; user token should be encrypted and stored
            User user = mUserViewModel.register(newUser);

            // TODO: if user does not exist, create one, otherwise notify user it already exists

            // TODO: if the created user is valid, proceed to event list (User obtained from ViewModel)
            if (user != null) {
                Navigation.findNavController(buttonView).navigate(R.id.navigation_event_list);
            }
            else {
                // show error stating the user does not exist or is otherwise invalid
                createToast(rootView, getString(R.string.toast_invalid_login)).show();
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

            // TODO: check if user exists
            //User user = mUserViewModel.findUser(username, password);
            User authenticatingUser = new User();
            authenticatingUser.username = username;
            authenticatingUser.password = password;
            User user = mUserViewModel.login(authenticatingUser);

            // TODO: the user should be set on the event list and
            //  the rest of the app so it knows whether the user can access it
            //  the create account handler should behave this way as well

            // if user exists, login; otherwise, cancel
            //if (user != null) {
            //    user.loggedIn = true;
            //    user = mUserViewModel.updateUser(user);
            //}
            //else {
            //    // show error stating the user does not exist or is otherwise invalid
            //    createToast(rootView, getString(R.string.toast_invalid_login)).show();
            //    return;
            //}

            // // if the logged in user is valid, proceed to event list
            if (user != null) {
                Navigation.findNavController(buttonView).navigate(R.id.navigation_event_list);
            }
            else {
                // show error stating the user does not exist or is otherwise invalid
                createToast(rootView, getString(R.string.toast_invalid_login)).show();
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