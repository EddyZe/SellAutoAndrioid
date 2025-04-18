package com.example.sellauto.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.sellauto.R
import com.example.sellauto.clients.sellauto.SellAutoClient
import com.example.sellauto.databinding.LoginFragmentBinding
import com.example.sellauto.databinding.ProfileFragmentBinding
import com.example.sellauto.exception.SellAutoApiException
import com.example.sellauto.exception.UnAuthException
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: ProfileFragmentBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("profile == null")


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ProfileFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sellAutoClient = SellAutoClient(view.context)

        binding.logoutButton.setOnClickListener {
            lifecycleScope.launch {
                try {
                    view.findNavController()
                        .navigate(R.id.loginFragment)
                    sellAutoClient.logout()
                } catch (e: Exception) {
                    toast(e.message ?: "Неизвестная ошибка")
                }
            }
        }

        binding.myAdsButton.setOnClickListener {
            view.findNavController()
                .navigate(R.id.myAdsFragment)
        }

        lifecycleScope.launch {
            try {
                val profile = sellAutoClient.getMyProfile()
                binding.helloUser.text = buildString {
                    append(resources.getString(R.string.hello_user_profile))
                    append(", ")
                    append(profile.firstName)
                }

                binding.firstName.text = buildString {
                    append(resources.getString(R.string.user_first_name))
                    append(": ")
                    append(profile.firstName)
                }

                binding.lastName.text = buildString {
                    append(resources.getString(R.string.user_last_name))
                    append(": ")
                    append(profile.lastName)
                }

                binding.email.text = buildString {
                    append(resources.getString(R.string.user_email))
                    append(": ")
                    append(profile.account.email)
                }

                binding.phoneNumber.text = buildString {
                    append(resources.getString(R.string.user_phone_number))
                    append(": ")
                    append(profile.account.phoneNumber)
                }

                binding.rating.text = buildString {
                    append(resources.getString(R.string.user_rating))
                    append(": ")
                    append("%.1f".format(profile.rating))
                    append("⭐")
                }
            } catch (e: SellAutoApiException) {
                toast(e.message ?: "Неизвестная ошибка")
            } catch (e: UnAuthException) {
                toast("Чтобы просмотреть профиль необходимо войти")
                view.findNavController()
                    .navigate(R.id.loginFragment)
            } catch (e: Exception) {
                e.printStackTrace()
                toast("Что-то пошло не так! Повторите попытку")
            }
        }
    }

    private fun toast(message: String) =
        Toast.makeText(view?.context, message, Toast.LENGTH_LONG)
            .show()
}