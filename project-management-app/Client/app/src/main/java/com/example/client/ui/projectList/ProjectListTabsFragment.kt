package com.example.client.ui.projectList

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.client.R
import com.example.client.data.model.Project
import com.example.client.ui.taskList.EXTRA_PROJECT_ID
import com.example.client.ui.taskList.TaskListActivity
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator


const val ARG_KIND = "kind"
const val LOG = "ProjectListTabFragment"

class ProjectListTabsFragment : Fragment() {

    private val labels: Array<String> = arrayOf("Favorite", "All", "Deadline")

    private lateinit var projectListAdapter: ProjectListAdapter
    private lateinit var viewPager: ViewPager2

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_project_list_tabs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        projectListAdapter = ProjectListAdapter(this)
        viewPager = view.findViewById(R.id.view_pager)
        viewPager.adapter = projectListAdapter

        val tabLayout: TabLayout = view.findViewById(R.id.tabs)
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = labels[position]
        }.attach()

    }
}


class ProjectListAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val PROJECT_KIND: Array<String> = arrayOf("favorite", "modificationDate", "deadline")

    override fun createFragment(position: Int): Fragment {
        val fragment = ProjectListFragment()
        fragment.arguments = Bundle().apply {
            putString(ARG_KIND, PROJECT_KIND[position])
        }
        return fragment
    }

    override fun getItemCount(): Int = 3

}

class ProjectListFragment: Fragment(), ProjectRecyclerViewAdapter.OnListFragmentInteractionListener  {

    private lateinit var projectRecyclerViewAdapter: ProjectRecyclerViewAdapter
    private lateinit var projectRecyclerView: RecyclerView
    private lateinit var viewModel: ProjectViewModel

    override fun onListFragmentInteraction(item: Project?) {
        val intent = Intent(this.context, TaskListActivity::class.java)
        intent.putExtra(EXTRA_PROJECT_ID, item!!.id)
        startActivity(intent)
        Log.d(LOG, "List fragment interaction")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val fragment: Fragment = this
        arguments?.takeIf { it.containsKey(ARG_KIND) }?.apply {

            val kind: String = getString(ARG_KIND)!!

            activity?.let {
                viewModel = ViewModelProviders.of(fragment, ProjectViewModelFactory(kind)).get<ProjectViewModel>(ProjectViewModel::class.java)
            } ?: throw Exception("Invalid Activity")

        }

        return inflater.inflate(R.layout.fragment_project_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val listener = this

        arguments?.takeIf { it.containsKey(ARG_KIND) }?.apply {

            viewModel.getProjects().observe(viewLifecycleOwner, Observer { projects ->

                if(projects != null){
                    projectRecyclerViewAdapter = ProjectRecyclerViewAdapter(projects, listener, viewModel)
                    projectRecyclerView = view.findViewById(R.id.list)
                    projectRecyclerView.adapter = projectRecyclerViewAdapter
                }
                else{
                    Log.d(LOG, "ProjectListFragment onViewCreated no project")
                }

            })

        }

    }



}


