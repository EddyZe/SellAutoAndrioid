package com.example.sellauto.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.view.menu.MenuItemImpl
import androidx.appcompat.view.menu.MenuView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.sellauto.R
import com.example.sellauto.clients.sellauto.SellAutoClient
import com.example.sellauto.databinding.LoginFragmentBinding
import com.example.sellauto.exception.UnAuthException
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: LoginFragmentBinding? = null
    private val binding
        get() = _binding ?: throw RuntimeException("LoginFragment == null")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LoginFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sellAutoClient = SellAutoClient(view.context)
        val email = binding.username
        val password = binding.password
        val loginBtn = binding.login

        loginBtn.setOnClickListener {
            val emailVal = email.text.toString()
            if (emailVal.isEmpty()) {
                toast("Email не может быть пустым")
                return@setOnClickListener
            }
            val passwordVal = password.text.toString()
            if (passwordVal.isEmpty()) {
                toast("Пароль не может быть пустым")
                return@setOnClickListener
            }


            lifecycleScope.launch {
                try {
                    sellAutoClient.login(emailVal, passwordVal)
                    it.findNavController()
                        .navigate(R.id.profileFragment)
                    val item = view.findViewById<View>(R.id.myAdsFragment)
                    item.isVisible = true

                } catch (e: UnAuthException) {
                    toast("Не верный email или пароль")
                } catch (e: Exception) {
                    e.printStackTrace()
                    toast("Что-то пошло не так!")
                }
            }
        }

    }

    private fun toast(message: String) =
        Toast.makeText(view?.context, message, Toast.LENGTH_LONG)
            .show()
}