package asha.binar.challengechapterlimabinarasha.view.fragment

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.Navigation
import asha.binar.challengechapterlimabinarasha.R
import asha.binar.challengechapterlimabinarasha.data.utils.Status
import asha.binar.challengechapterlimabinarasha.databinding.FragmentRegisterBinding
import asha.binar.challengechapterlimabinarasha.model.users.PostUserResponse
import asha.binar.challengechapterlimabinarasha.view.dialogfragment.RegisterWaitDialogFragment
import asha.binar.challengechapterlimabinarasha.viewmodel.UserApiViewModel
import java.util.regex.Pattern


class RegisterFragment : Fragment(), View.OnClickListener  {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private val viewModelUser: UserApiViewModel by hiltNavGraphViewModels(R.id.nav_main)

    private lateinit var name: String
    private lateinit var email: String
    private lateinit var password: String
    private var cek: Boolean = false
    private var viewPass: Boolean = false
    private var viewKonfPass: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity?)!!.supportActionBar?.title = "Register"

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            btnRegister.setOnClickListener(this@RegisterFragment)
            btnViewPass.setOnClickListener(this@RegisterFragment)
            btnViewKonfPass.setOnClickListener(this@RegisterFragment)
        }
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.btn_register -> {
                register()
            }
            R.id.btn_view_pass -> {
                if (!viewPass) {
                    binding.apply {
                        btnViewPass.setImageResource(R.drawable.ic_green_eye_24)
                        inputPassword.transformationMethod =
                            HideReturnsTransformationMethod.getInstance()
                    }
                    viewPass = true
                } else {
                    binding.apply {
                        btnViewPass.setImageResource(R.drawable.ic_outline_remove_green_eye_24)
                        inputPassword.transformationMethod =
                            PasswordTransformationMethod.getInstance()
                    }
                    viewPass = false
                }
            }
            R.id.btn_view_konf_pass -> {
                if (!viewKonfPass) {
                    binding.apply {
                        btnViewKonfPass.setImageResource(R.drawable.ic_green_eye_24)
                        inputKonfPassword.transformationMethod =
                            HideReturnsTransformationMethod.getInstance()
                    }
                    viewKonfPass = true
                } else {
                    binding.apply {
                        btnViewKonfPass.setImageResource(R.drawable.ic_outline_remove_green_eye_24)
                        inputKonfPassword.transformationMethod =
                            PasswordTransformationMethod.getInstance()
                    }
                    viewKonfPass = false
                }
            }
        }
    }

    private fun register() {
        binding.apply {
            name = inputNama.text.toString()
            email = inputEmail.text.toString()
            password = inputPassword.text.toString()
            cek = isValidEmail(email)
        }

        if (inputCheck(name, email, password, cek)) {
            registerUser(name, email, password)
        }
    }

    private fun registerUser(name: String, email: String, password: String) {
        val regUser = PostUserResponse(
            "",
            "",
            email,
            "",
            password,
            name,
        )

        if (inputCheck(name, email, password, cek)) {

            viewModelUser.getUser(email).observe(viewLifecycleOwner) {
                when (it.status) {
                    Status.SUCCESS -> {
                        val data = it.data!!
                        if (data.isNotEmpty()) {
                            viewModelUser.registerCheck.postValue(true)
                            Toast.makeText(
                                requireContext(),
                                "Email has already used by another user",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            viewModelUser.registerUser(regUser).observe(viewLifecycleOwner) { reg ->
                                when (reg.status) {
                                    Status.SUCCESS -> {
                                        viewModelUser.registerCheck.postValue(true)
                                        Toast.makeText(
                                            requireContext(),
                                            "Register successful",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        Navigation.findNavController(requireView())
                                            .navigate(R.id.action_registerFragment_to_loginFragment)
                                    }
                                    Status.ERROR -> {
                                        viewModelUser.registerCheck.postValue(true)
                                        Log.d("errMsg", reg.message.toString())
                                    }
                                    Status.LOADING -> Log.d("loadingMsg", reg.message.toString())
                                }
                            }
                        }
                    }
                    Status.ERROR -> Log.d("errMsg", it.message.toString())
                    Status.LOADING -> RegisterWaitDialogFragment().show(requireActivity().supportFragmentManager, null)
                }

            }
        }
    }


    private fun inputCheck(name: String, email: String, password: String, cek: Boolean): Boolean {
        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || !cek
            || binding.inputKonfPassword.text.toString() != password || password.length < 6
        ) {
            if (name.isEmpty()) {
                binding.apply {
                    inputNama.error = "Username can't be empty"
                    inputNama.requestFocus()
                }

            }
            if (email.isEmpty()) {
                binding.apply {
                    inputEmail.error = "Email can't be empty"
                    inputEmail.requestFocus()
                }
            }
            if (password.isEmpty()) {
                binding.apply {
                    inputPassword.error = "Password can't be empty"
                    inputPassword.requestFocus()
                }
            }
            if (!cek) {
                binding.apply {
                    inputEmail.error = "Incorrect email format"
                    inputEmail.requestFocus()
                }
            }
            if (binding.inputKonfPassword.text.toString() != password) {
                binding.apply {
                    inputKonfPassword.error = "Wrong password"
                    inputKonfPassword.requestFocus()
                }

            }
            if (password.length < 6) {
                binding.apply {
                    inputPassword.error = "Minimum 6 characters"
                    inputPassword.requestFocus()
                }
            }
            return false
        } else {
            return true
        }
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = Pattern.compile(
            "[a-zA-Z0-9+._%\\-]{1,256}" +
                    "@" +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                    "(" +
                    "\\." +
                    "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                    ")+"
        )
        return emailPattern.matcher(email).matches()
    }
}