package asha.binar.challengechapterlimabinarasha.view.fragment

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.hilt.navigation.fragment.hiltNavGraphViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import asha.binar.challengechapterlimabinarasha.R
import asha.binar.challengechapterlimabinarasha.data.datastore.DataStoreManager
import asha.binar.challengechapterlimabinarasha.data.utils.Status
import asha.binar.challengechapterlimabinarasha.databinding.FragmentHomeBinding
import asha.binar.challengechapterlimabinarasha.model.nowplaying.ResultNow
import asha.binar.challengechapterlimabinarasha.model.popularmovie.ResultMovie
import asha.binar.challengechapterlimabinarasha.view.adapter.MovieAdapter
import asha.binar.challengechapterlimabinarasha.view.adapter.MovieLinearAdapter
import asha.binar.challengechapterlimabinarasha.view.adapter.NowPlayingAdapter
import asha.binar.challengechapterlimabinarasha.viewmodel.MovieApiViewModel
import asha.binar.challengechapterlimabinarasha.viewmodel.UserApiViewModel
import java.util.*


class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModelMovie: MovieApiViewModel by hiltNavGraphViewModels(R.id.nav_main)
    private val viewModelUser: UserApiViewModel by hiltNavGraphViewModels(R.id.nav_main)
    private lateinit var pref: DataStoreManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        (activity as AppCompatActivity?)!!.supportActionBar?.show()
        (activity as AppCompatActivity?)!!.supportActionBar?.title = ""
        pref = DataStoreManager(requireContext())


        viewModelUser.getEmail().observe(viewLifecycleOwner) {
            val email = it

            viewModelUser.getUser(email).observe(viewLifecycleOwner){ user ->
                when (user.status){
                    Status.SUCCESS -> {
                        val data = user.data!![0]
                        (activity as AppCompatActivity?)!!.supportActionBar?.title =
                            "Welcome, ${
                                data.username.replaceFirstChar{ userData ->
                                    if (userData.isLowerCase()) userData.titlecase(
                                        Locale.getDefault()
                                    ) else userData.toString()
                                }
                            }!"
                        viewModelUser.user.postValue(data)
                    }
                    Status.ERROR -> Toast.makeText(requireContext(), user.message, Toast.LENGTH_SHORT)
                        .show()
                    Status.LOADING -> Log.d("loadingMsg", "Loading")
                }
            }
        }
        val ai: ApplicationInfo = requireActivity().applicationContext.packageManager
            .getApplicationInfo(
                requireActivity().applicationContext.packageName,
                PackageManager.GET_META_DATA
            )
        val values = ai.metaData["apiKey"]

        viewModelMovie.apiKey.value = values.toString()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        viewModelMovie.getBoolean().observe(viewLifecycleOwner) {
            val cek = it
            binding.switchRv.isChecked = cek

            viewModelMovie.getPopularMovie().observe(viewLifecycleOwner) { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        if (cek) {
                            showListLinear(resource.data!!.resultMovies)
                        } else {
                            showList(resource.data!!.resultMovies)
                        }
                        binding.switchRv.setOnCheckedChangeListener { _, isChecked ->
                            if (isChecked) {
                                viewModelMovie.setBoolean(true)
                                showListLinear(resource.data.resultMovies)
                            } else {
                                viewModelMovie.setBoolean(false)
                                showList(resource.data.resultMovies)
                            }
                        }
                        binding.progressCircular.visibility = View.GONE
                    }
                    Status.ERROR -> {
                        Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT)
                            .show()
                    }
                    Status.LOADING -> {
                        Log.d("loadingMsg", "Loading")
                    }
                }
            }
        }

        viewModelMovie.getNowPlaying().observe(viewLifecycleOwner) {
            when (it.status) {
                Status.SUCCESS -> {
                    showListNowPlay(it.data!!.resultNows)
                }
                Status.ERROR -> {
                    Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                }
                Status.LOADING -> {
                    Log.d("loadingMsg", "Loading")
                }
            }
        }
    }

    private fun showListLinear(it: List<ResultMovie>?) {
        binding.rvMovie.layoutManager = LinearLayoutManager(requireContext())
        val adapter = MovieLinearAdapter(object : MovieLinearAdapter.OnClickListener {
            override fun onClickItem(data: ResultMovie) {
                viewModelMovie.id.postValue(data.id)
                Navigation.findNavController(requireView())
                    .navigate(R.id.action_homeFragment_to_detailFragment)
            }
        })
        adapter.submitData(it)
        binding.rvMovie.adapter = adapter
    }

    private fun showListNowPlay(it: List<ResultNow>?) {
        binding.rvNow.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val adapter = NowPlayingAdapter(object : NowPlayingAdapter.OnClickListener {
            override fun onClickItem(data: ResultNow) {
                viewModelMovie.id.postValue(data.id)
                Navigation.findNavController(requireView())
                    .navigate(R.id.action_homeFragment_to_detailFragment)
            }
        })
        adapter.submitData(it)
        binding.rvNow.adapter = adapter
    }

    private fun showList(data: List<ResultMovie>?) {
        binding.rvMovie.isNestedScrollingEnabled = false
        binding.rvMovie.layoutManager = GridLayoutManager(requireContext(), 2)
        val adapter = MovieAdapter(object : MovieAdapter.OnClickListener {
            override fun onClickItem(data: ResultMovie) {
                viewModelMovie.id.postValue(data.id)
                Navigation.findNavController(requireView())
                    .navigate(R.id.action_homeFragment_to_detailFragment)
            }
        })
        adapter.submitData(data)
        binding.rvMovie.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.profile -> {
                viewModelUser.user.observe(viewLifecycleOwner) {
                    Navigation.findNavController(requireView())
                        .navigate(R.id.action_homeFragment_to_profileFragment)
                }
                true
            }
            else -> true
        }
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.option_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


}