package com.example.university.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.university.*
import com.example.university.databinding.FragmentHomeBinding
import com.example.university.model.HospitalItem
import com.example.university.model.HospitalMarker
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import kotlinx.coroutines.launch

class HomeFragment : Fragment(), OnMapReadyCallback {

    private val mainViewModel: MainViewModel by activityViewModels()

    private lateinit var binding : FragmentHomeBinding

    private lateinit var naverMap: NaverMap
    private val markers = mutableListOf<HospitalMarker>()
    private val viewModel: MainViewModel by activityViewModels()
    private val mapsViewModel: MapsViewModel by viewModels()
    private val infoWindow = InfoWindow()
    private var mapFragment: MapFragment? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        Log.d("HomeFragment", "onCreateView")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        binding.talkTap.setOnClickListener{
            viewModel.stop()
            it.findNavController().navigate(R.id.action_homeFragment_to_talkFragment)
        }

        binding.bookmarkTap.setOnClickListener{
            viewModel.stop()
            it.findNavController().navigate(R.id.action_homeFragment_to_bookmarkFragment)
        }

        binding.searchEdit.addTextChangedListener {
            mainViewModel.hospitalName.postValue(it.toString())
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("HomeFragment", "onViewCreated")

        val fm = requireActivity().supportFragmentManager
        mapFragment = binding.mapFragment as? MapFragment
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map_fragment, it).commit()
            }

        mapFragment?.getMapAsync(this)

        val recyclerView = binding.departmentRecyclerView
        val adapter = MedicalDepartmentAdapter(requireContext(), Constant.DEPARTMENTS, mapsViewModel)
        recyclerView.adapter = adapter

        viewModel.locationSource = FusedLocationSource(requireActivity(), MainActivity.LOCATION_PERMISSION_REQUEST_CODE)

        lifecycleScope.launch {
            viewModel.startGetHospitals()
        }

        viewModel.items.observe(requireActivity()) {
            if (this::naverMap.isInitialized)
                loadMap(it)
        }

        viewModel.locationState.observe(requireActivity(), Observer {
            Log.e("HomeFragment", "state : $it")
            naverMap.locationTrackingMode = it
        })

        mapsViewModel.selectedDepartment.observe(requireActivity()) {
            resetMarker()
            viewModel.apply {
                targetDepartment = if (it != null) "%02d".format(it.code) else ""
            }.startGetHospitals()
        }

        viewModel.hospitalName.observe(requireActivity()) {
            Log.e("TEST", "search: ${it}")
            resetMarker()
            viewModel.startGetHospitals()
        }
    }

    private fun loadMap(it: List<HospitalItem>?) {
        it?.forEach { item ->
            val x = item.xPos
            val y = item.yPos
            if (x != null && y != null) {
                setMarker(x, y, item)
            }
        }
        viewModel.nextStep()
    }

    override fun onMapReady(p0: NaverMap) {
        Log.e("HomeFragment", "onMapReady")
        naverMap = p0
        naverMap.locationSource = viewModel.locationSource
        naverMap.uiSettings.isLocationButtonEnabled = true
        naverMap.setOnMapClickListener { _, _ ->
            infoWindow.close()
        }
        loadMap(viewModel.items.value)
    }

    fun setMarker(x: String, y: String, item: HospitalItem) {
        val marker = Marker()
        marker.icon = OverlayImage.fromResource(R.drawable.map_marker)
        marker.width = 50 // 테스트 때문에 사이즈 임의로 변경했습니다
        marker.height = 75
        marker.position = LatLng(y.toDouble(), x.toDouble())
        marker.map = naverMap

        // 테스트를 위해서 captionText 추가
        marker.captionText = item.yadmNm ?: ""

        // 테스트를 위해서 infoWindow 추가
        marker.onClickListener = Overlay.OnClickListener {
            if (marker.infoWindow == null) {
                // 현재 마커에 정보 창이 열려있지 않을 경우 엶
                infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(requireContext()) {
                    override fun getText(infoWindow: InfoWindow): CharSequence =
                        item.yadmNm ?: "정보 없음"
                }
                infoWindow.open(marker)
            } else {
                // 이미 현재 마커에 정보 창이 열려있을 경우 닫음
                infoWindow.close()
            }
            true
        }
        markers.add(HospitalMarker(marker, item))
    }

    // 지도에 등록된 마커 초기화
    private fun resetMarker() {
        markers.forEach {
            it.marker.map = null
        }
        markers.clear()
    }
}