package br.com.onibusapp.onibusapp.ui.pesquisa

import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import br.com.onibusapp.onibusapp.R
import br.com.onibusapp.onibusapp.data.dao.FavoritoDAO
import br.com.onibusapp.onibusapp.data.dao.LinhaDAO
import br.com.onibusapp.onibusapp.data.dominio.Empresa
import br.com.onibusapp.onibusapp.data.dominio.Empresas
import br.com.onibusapp.onibusapp.data.dominio.Filtro
import com.google.firebase.database.*


class PesquisarFragment : Fragment(), PesquisarContract.View {

    private lateinit var mPresenter: PesquisarContract.Presenter
    private var linhaDAO: LinhaDAO? = null
    private var favoritoDAO: FavoritoDAO? = null

    private var spEmpresa: Spinner? = null
    private var spLinha: Spinner? = null
    private var spSentido: Spinner? = null
    private var cbxAddFavorititos: CheckBox? = null
    private var btnPesquisar: Button? = null

    private var linhaDataAdapter: ArrayAdapter<String>? = null
    private var empresaDataAdapter: ArrayAdapter<String>? = null


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_pesquisar, container, false)
        spEmpresa = view.findViewById<View>(R.id.sp_empresa) as Spinner
        spLinha = view.findViewById<View>(R.id.sp_linha) as Spinner
        spSentido = view.findViewById<View>(R.id.sp_sentido) as Spinner
        cbxAddFavorititos = view.findViewById<View>(R.id.cbx_add_favoritos) as CheckBox
        btnPesquisar = view.findViewById<View>(R.id.btn_pesquisar) as Button
        linhaDAO = LinhaDAO(activity)
        favoritoDAO = FavoritoDAO(activity)

        var fireBase = FirebaseDatabase.getInstance()
        fireBase.setPersistenceEnabled(true)

        mPresenter = PesquisarPresenter(this, linhaDAO!!, favoritoDAO!!, fragmentManager, fireBase.reference)

        addEventos()
        return view
    }

    private fun addEventos() {
        addEventoSelecionarEmpresa()
        addPesquisarEventos()
    }

    private fun addEventoSelecionarEmpresa() {
        spEmpresa!!.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                mPresenter.selecionarEmpresa(position)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    private fun addPesquisarEventos() {
        btnPesquisar!!.setOnClickListener { mPresenter!!.pesquisar() }
    }


    override fun atualizarSpinnerLinha(nomes: MutableList<String>) {
        linhaDataAdapter!!.clear()
        linhaDataAdapter!!.addAll(nomes)
        linhaDataAdapter!!.notifyDataSetChanged()
    }

    override fun criarDefaultAdapterLinha(linhas: List<String>) {
        linhaDataAdapter = ArrayAdapter(activity, android.R.layout.simple_spinner_item, linhas)
        linhaDataAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spLinha!!.adapter = linhaDataAdapter
    }

    override fun criarDefaultAdapterEmpresa(empresas: List<String>) {
        empresaDataAdapter = ArrayAdapter(activity, android.R.layout.simple_spinner_item, empresas)
        empresaDataAdapter!!.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spEmpresa!!.adapter = empresaDataAdapter
    }

    override fun selecionarFiltros(): Filtro {
        val linha = spLinha!!.selectedItem.toString()
        val empresa = spEmpresa!!.selectedItem.toString()
        if (linha == PesquisarContract.Presenter.NENHUMA_LINHA_ENCONTRADA) {

        }

        val sentido = spSentido!!.selectedItemPosition
        val adicionarFavoritos = cbxAddFavorititos!!.isChecked
        var url = this.mPresenter!!.recuperarUrlEmpresa()
        return Filtro(linha, sentido, adicionarFavoritos, url)
    }

    override fun onDestroy() {
        super.onDestroy()
        linhaDAO = null
        favoritoDAO = null
    }

    override fun onStart() {
        super.onStart()
    }

}
