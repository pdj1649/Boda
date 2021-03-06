**TIL > Android**

<br>

## π₯ TIL: Android

> * μμ±μ: Sanghyun Park
> * μ΅κ·Ό μμ μΌμ: 2022. **03. 04.** (κΈ)



<br>

#### 001. λ¦¬μ€λ μ΄ν΄νκΈ°

<br>

```kotlin
// listener μμ±
val listener = object : View.OnClickListener {
    // `ctrl + i`: overriding ν΄μΌ νλ λ©μλ νμΈ κ°λ₯ 
    override fun onClick(p0: View?) {
        Log.d("ClickListener", "ν΄λ¦­ λμμ΅λλ€.")
    }
}

// listener μ¬μ©
with (binding) {
    button.setOnClickListener(listener)
}

// listenerμ λ©μλκ° ν κ°μΈ κ²½μ°, λλ€μμ²λΌ μ¬μ© κ°λ₯
with (binding) {
    button.setOnClickListener {
        Log.d("ClickListener", "ν΄λ¦­ λμμ΅λλ€.")
    }
}
```



<br>

#### 002. Values Resource File

<br>

```kotlin
// λμ΄, λλΉ, ν¬κΈ°
<dimen name="hei_btn">80dp</dimen>
<dimen name="size_text">20sp</dimen>

// μ
<color name="text">#F86496</color>

// μ€νΈλ§(content)
<string name="tView_content">Hello, Android and Kotlin</string>
```



<br>

#### 003. RecyclerView

<br>

```kotlin
class MainActivity : AppCompatActivity() {

    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // 1. load data
        val memoList = loadMemos()

        // 2. create custom adapter
        val customAdapter = CustomAdapter(memoList)

        // 3. binding
        binding.recyclerView.adapter = customAdapter

        // 4. layout manager
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

    }

    fun loadMemos(): MutableList<Memo> {
        val memoList = mutableListOf<Memo>()
        for (idx in 1..100) {
            val title = "μ΄κ²μ΄ μλλ‘μ΄λλ€ $idx"
            val date = System.currentTimeMillis()
            val memo = Memo(idx, title, date)
            memoList.add(memo)
        }

        return memoList
    }
}

class CustomAdapter(val memoList: MutableList<Memo>) : RecyclerView.Adapter<CustomAdapter.Holder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemRecyclerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        // 1. μ¬μ©ν  λ°μ΄ν° μΆμΆ
        val memo = memoList.get(position)

        // 2. ν΄λΉ λ°μ΄ν° νλμ μ λ¬
        holder.setMemo(memo)
    }

    override fun getItemCount() = memoList.size

    class Holder(val binding: ItemRecyclerBinding) : RecyclerView.ViewHolder(binding.root) {
        var currentMemo: Memo? = null

        // λΆνμν νΈμΆμ λ§κΈ° μν΄ initμ ν΄λ¦­λ¦¬μ€λ μμ±
        init {
            binding.root.setOnClickListener {
                Toast.makeText(binding.root.context, "ν΄λ¦­λ μμ΄ν: ${currentMemo?.title}", Toast.LENGTH_SHORT).show()
            }
        }

        // 3. representation
        fun setMemo(memo: Memo) {
            currentMemo = memo
            with (binding) {
                textNo.text = "${memo.no}"
                textTitle.text = memo.title

                val sdf = SimpleDateFormat("yyyy-dd-mm")
                val formattedDate = sdf.format(memo.timestamp)
                textDate.text = formattedDate
            }
        }
    }
}
```



<br>

#### 004. Fragment

<br>

**MainActivity.kt**

```kotlin
class MainActivity : AppCompatActivity() {

    val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    // 1. fragment μμ±
    val listFragment by lazy { ListFragment() }
    val detailFragment by lazy { DetailFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setFragment()

        binding.btnSend.setOnClickListener {
            listFragment.setValue("κ° μ λ¬νκΈ°")
        }
    }

    fun goDetail() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.frameLayout, detailFragment)
        transaction.addToBackStack("detail")
        transaction.commit()
    }

    fun goBack() {
        onBackPressed()
    }

    fun setFragment() {
        val bundle = Bundle()
        bundle.putString("key1", "List Fragment")
        bundle.putInt("key2", 20220305)

        listFragment.arguments = bundle

        // 2. transaction
        val transaction = supportFragmentManager.beginTransaction()

        // 3. inserting fragments by transaction
        transaction.add(R.id.frameLayout, listFragment)
        transaction.commit()
    }
}
```

<br>

**ListFragment.kt**

```kotlin
class ListFragment : Fragment() {

    lateinit var binding: FragmentListBinding
    lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is MainActivity) mainActivity = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with (binding) {
            arguments?.apply {
                textTitle.text = getString("key1")
                textValue.text = "${getInt("key2")}"
            }

            btnNext.setOnClickListener {
                mainActivity.goDetail()
            }
        }
    }

    fun setValue(value: String) {
        binding.textFromActivity.text = value
    }
}
```

<br>

**DetailFragment.kt**

```kotlin
class DetailFragment : Fragment() {

    lateinit var binding: FragmentDetailBinding
    lateinit var mainActivity: MainActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is MainActivity) mainActivity = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            mainActivity.goBack()
        }
    }
}
```

