package com.zybooks.eventtracker;

//import static com.zybooks.eventtracker.backgroundservice.EncryptedDataStore.PREF_FILE_NAME;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.zybooks.eventtracker.apiservice.EncryptedJWTStore;
import com.zybooks.eventtracker.apiservice.RemoteRepository;
//import com.zybooks.eventtracker.dbmodel.User;
import com.zybooks.eventtracker.clientmodel.User;
import com.zybooks.eventtracker.viewmodel.UserViewModel;
import com.zybooks.eventtracker.viewmodel.UserViewModelFactory;

// TODO: the user should be set on the event list and
//  the rest of the app so it knows whether the user can access it
//  the create account and login handlers should behave this way as well

public class LoginFragment extends Fragment {
    private static LoginFragment sFragment;
    private static EncryptedJWTStore sJWTStore;
    private UserViewModel mUserViewModel;

    public LoginFragment() {
        // Required empty public constructor
    }

    public static LoginFragment newInstance() {
        if (sFragment == null) {
            sFragment = new LoginFragment();
        }

        return sFragment;
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

        // set up repository with encrypted JWT keystore located in shared preferences
        Application app = requireActivity().getApplication();
        final SharedPreferences sharedPreferences =
                app.getSharedPreferences(
                        app.getString(R.string.pref_file_name), Context.MODE_PRIVATE);
        sJWTStore = new EncryptedJWTStore(sharedPreferences);

        // set up viewmodel
        RemoteRepository remoteRepo = new RemoteRepository(sJWTStore);
        UserViewModelFactory factory =
                new UserViewModelFactory(app, remoteRepo);
        mUserViewModel = new ViewModelProvider(this, factory).get(UserViewModel.class);

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

            // attempt registration and check if user exists
            User newUser = User.createDefault();
            newUser.username = username;
            newUser.password = password;

            // register user; user token should be encrypted and stored
            MutableLiveData<User> userData = mUserViewModel.register(newUser);

            // observe LiveData
            // https://gullapalli-nikitha.medium.com/retrofit-implementation-using-mvvm-architecture-b7f553f39643
            userData.observe(
                getViewLifecycleOwner(),
                new Observer<User>() {
                    @Override
                    public void onChanged(User userOutput) {
                        // proceed to event list if user is valid and has id, otherwise make toast
                        if (userOutput != null && !userOutput.remoteId.isEmpty()) {
                            Navigation
                                    .findNavController(buttonView)
                                    .navigate(R.id.navigation_event_list);
                        }
                    }
                }
            );
            MutableLiveData<String> msgData = mUserViewModel.message();
            msgData.observe(
                getViewLifecycleOwner(),
                new Observer<String>() {
                    @Override
                    public void onChanged(String msgOutput) {
                        // show error stating the user does not exist or is otherwise invalid
                        String msg = mUserViewModel.message().getValue();

                        if ((msg != null) && !msg.isEmpty()) {
                            createToast(rootView, msg).show();
                        }
                        else {
                            createToast(rootView, getString(R.string.toast_invalid_user)).show();
                        }
                    }
                }
            );
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

            // attempt login and check if user exists
            User authenticatingUser = User.createDefault();
            authenticatingUser.username = username;
            authenticatingUser.password = password;

            // attempt to log in user; user token should be encrypted and stored
            MutableLiveData<User> userData = mUserViewModel.login(authenticatingUser);

            // observe LiveData
            // https://gullapalli-nikitha.medium.com/retrofit-implementation-using-mvvm-architecture-b7f553f39643
            userData.observe(
                getViewLifecycleOwner(),
                new Observer<User>() {
                    @Override
                    public void onChanged(User userOutput) {
                        // proceed to event list if user is valid and has id, otherwise make toast
                        if (userOutput != null && !userOutput.remoteId.isEmpty()) {
                            Navigation
                                    .findNavController(buttonView)
                                    .navigate(R.id.navigation_event_list);
                        }
                    }
                }
            );

            MutableLiveData<String> msgData = mUserViewModel.message();

            msgData.observe(
                getViewLifecycleOwner(),
                new Observer<String>() {
                    @Override
                    public void onChanged(String msgOutput) {
                        // show error stating the user does not exist or is otherwise invalid
                        String msg = mUserViewModel.message().getValue();

                        if ((msg != null) && !msg.isEmpty()) {
                            createToast(rootView, msg).show();
                        }
                        else {
                            createToast(rootView, getString(R.string.toast_invalid_user)).show();
                        }
                    }
                }
            );
        });

        return rootView;
    }

    private Toast createToast (View rootView, String msg) {
        return Toast.makeText(
                rootView.getContext(),
                msg,
                Toast.LENGTH_SHORT);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        getParentFragmentManager()
            .beginTransaction().remove((Fragment)this).commitAllowingStateLoss();
    }
}
