package com.example.proyect.fragments.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.example.proyect.R
import com.example.proyect.adapters.HomeViewPagesAdapter
import com.example.proyect.databinding.FragmentHomeBinding
import com.example.proyect.fragments.categories.*
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment: Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoriesFragments = arrayListOf<Fragment>(
            MainCategoryFragment(),
            BlouseFragment(),
            DressFragment(),
            ShirtFragment(),
            SweatshirtFragment(),
            AccesoriesFragment()

        )

        val viewPager2Adapter =
            HomeViewPagesAdapter(categoriesFragments,childFragmentManager,lifecycle)
        binding.viewPager.adapter = viewPager2Adapter
        TabLayoutMediator(binding.tabLayout,binding.viewPager){ tab, position ->
            when(position){
                0 -> tab.text = "Inicio"
                1 -> tab.text= "Blusas"
                2 -> tab.text= "Vestidos"
                3 -> tab.text= "Guayaberas"
                4 -> tab.text= "Sudaderas"
                5 -> tab.text= "Accesorios"
            }
        }.attach()
    }
}