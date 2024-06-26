package com.ns.turkcellfinal.presentation.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.ns.turkcellfinal.core.base.BaseFragment
import com.ns.turkcellfinal.core.base.BaseResponse
import com.ns.turkcellfinal.core.domain.ViewState
import com.ns.turkcellfinal.core.util.gone
import com.ns.turkcellfinal.core.util.showToast
import com.ns.turkcellfinal.core.util.visible
import com.ns.turkcellfinal.databinding.FragmentProfileBinding
import com.ns.turkcellfinal.presentation.account.UserManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : BaseFragment<FragmentProfileBinding>(
    FragmentProfileBinding::inflate
) {

    private val viewModel: ProfileViewModel by viewModels()

    @Inject
    lateinit var userManager: UserManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initClick()
        getData()
    }

    private fun getData() {
        with(binding) {
            viewModel.getUserInfo(userManager.user?.token ?: "")

            viewLifecycleOwner.lifecycleScope.launch {

                viewModel.user.collect { response ->
                    when (response) {
                        is ViewState.Loading -> {
                            clProfile.gone()
                            progressBar.visible()
                        }

                        is ViewState.Success -> {
                            val result = response.result as BaseResponse.Success
                            val data = result.data

                            clProfile.visible()
                            progressBar.gone()

                            etName.setText("${data.firstName} ${data.lastName}")
                            etUsername.setText(data.username)
                            etEmail.setText(data.email)

                            Glide.with(requireContext())
                                .load(data.image)
                                .into(ivProfileImage)

                        }

                        is ViewState.Error -> {
                            requireContext().showToast(response.error)
                            progressBar.gone()
                        }
                    }
                }
            }
        }
    }

    private fun initClick() {
        with(binding) {
            toolbar.backButton.setOnClickListener {
                findNavController().navigateUp()
            }
        }
    }
}