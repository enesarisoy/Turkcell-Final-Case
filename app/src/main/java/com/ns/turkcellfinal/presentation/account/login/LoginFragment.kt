package com.ns.turkcellfinal.presentation.account.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.fragment.findNavController
import com.ns.turkcellfinal.R
import com.ns.turkcellfinal.core.base.BaseFragment
import com.ns.turkcellfinal.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(
    FragmentLoginBinding::inflate
) {

    private lateinit var composeView: ComposeView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).also {
            composeView = it
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        composeView.setContent {
            LoginScreen(
                onLoginClick = {
                    findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                }
            )
        }
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    LoginScreen(onLoginClick = {})
}