// This is automatically generated by the Tiger compiler.
// Do NOT modify!

// structures
struct TreeVisitor
{
    struct TreeVisitor_vtable *vptr;
};

struct TV
{
    struct TV_vtable *vptr;
};

struct Tree
{
    struct Tree_vtable *vptr;
    struct Tree * left;
    struct Tree * right;
    int key;
    int has_left;
    int has_right;
    struct Tree * my_null;
};

struct Visitor
{
    struct Visitor_vtable *vptr;
    struct Tree * l;
    struct Tree * r;
};

struct MyVisitor
{
    struct MyVisitor_vtable *vptr;
    struct Tree * l;
    struct Tree * r;
};

// vtables structures
struct TreeVisitor_vtable
{
};

struct TV_vtable
{
    int(*Start)(struct TV*);
};

struct Tree_vtable
{
    int(*Init)(struct Tree*, int);
    int(*SetRight)(struct Tree*, struct Tree *);
    int(*SetLeft)(struct Tree*, struct Tree *);
    struct Tree *(*GetRight)(struct Tree*);
    struct Tree *(*GetLeft)(struct Tree*);
    int(*GetKey)(struct Tree*);
    int(*SetKey)(struct Tree*, int);
    int(*GetHas_Right)(struct Tree*);
    int(*GetHas_Left)(struct Tree*);
    int(*SetHas_Left)(struct Tree*, int);
    int(*SetHas_Right)(struct Tree*, int);
    int(*Compare)(struct Tree*, int, int);
    int(*Insert)(struct Tree*, int);
    int(*Delete)(struct Tree*, int);
    int(*Remove)(struct Tree*, struct Tree *, struct Tree *);
    int(*RemoveRight)(struct Tree*, struct Tree *, struct Tree *);
    int(*RemoveLeft)(struct Tree*, struct Tree *, struct Tree *);
    int(*Search)(struct Tree*, int);
    int(*Print)(struct Tree*);
    int(*RecPrint)(struct Tree*, struct Tree *);
    int(*accept)(struct Tree*, struct Visitor *);
};

struct Visitor_vtable
{
    int(*visit)(struct Visitor*, struct Tree *);
};

struct MyVisitor_vtable
{
    int(*visit)(struct MyVisitor*, struct Tree *);
};

// method declarations
int TV_Start(struct TV * this);
int Tree_Init(struct Tree * this, int v_key);
int Tree_SetRight(struct Tree * this, struct Tree * rn);
int Tree_SetLeft(struct Tree * this, struct Tree * ln);
struct Tree * Tree_GetRight(struct Tree * this);
struct Tree * Tree_GetLeft(struct Tree * this);
int Tree_GetKey(struct Tree * this);
int Tree_SetKey(struct Tree * this, int v_key);
int Tree_GetHas_Right(struct Tree * this);
int Tree_GetHas_Left(struct Tree * this);
int Tree_SetHas_Left(struct Tree * this, int val);
int Tree_SetHas_Right(struct Tree * this, int val);
int Tree_Compare(struct Tree * this, int num1, int num2);
int Tree_Insert(struct Tree * this, int v_key);
int Tree_Delete(struct Tree * this, int v_key);
int Tree_Remove(struct Tree * this, struct Tree * p_node, struct Tree * c_node);
int Tree_RemoveRight(struct Tree * this, struct Tree * p_node, struct Tree * c_node);
int Tree_RemoveLeft(struct Tree * this, struct Tree * p_node, struct Tree * c_node);
int Tree_Search(struct Tree * this, int v_key);
int Tree_Print(struct Tree * this);
int Tree_RecPrint(struct Tree * this, struct Tree * node);
int Tree_accept(struct Tree * this, struct Visitor * v);
int Visitor_visit(struct Visitor * this, struct Tree * n);
int MyVisitor_visit(struct MyVisitor * this, struct Tree * n);

// vtables
struct TreeVisitor_vtable TreeVisitor_vtable_ = 
{
};

struct TV_vtable TV_vtable_ = 
{
    TV_Start,
};

struct Tree_vtable Tree_vtable_ = 
{
    Tree_Init,
    Tree_SetRight,
    Tree_SetLeft,
    Tree_GetRight,
    Tree_GetLeft,
    Tree_GetKey,
    Tree_SetKey,
    Tree_GetHas_Right,
    Tree_GetHas_Left,
    Tree_SetHas_Left,
    Tree_SetHas_Right,
    Tree_Compare,
    Tree_Insert,
    Tree_Delete,
    Tree_Remove,
    Tree_RemoveRight,
    Tree_RemoveLeft,
    Tree_Search,
    Tree_Print,
    Tree_RecPrint,
    Tree_accept,
};

struct Visitor_vtable Visitor_vtable_ = 
{
    Visitor_visit,
};

struct MyVisitor_vtable MyVisitor_vtable_ = 
{
    MyVisitor_visit,
};

// methods
int TV_Start(struct TV * this)
{
    struct Tree * root;
    int ntb;
    int nti;
    struct MyVisitor * v;
    struct Tree * x_1;
    struct Tree * x_2;
    struct Tree * x_3;
    struct Tree * x_4;
    struct Tree * x_5;
    struct Tree * x_6;
    struct Tree * x_7;
    struct Tree * x_8;
    struct Tree * x_9;
    struct Tree * x_10;
    struct Tree * x_11;
    struct Tree * x_12;
    struct Tree * x_13;
    struct Tree * x_14;
    struct Tree * x_15;
    struct Tree * x_16;
    struct Tree * x_17;
    struct Tree * x_18;
    struct Tree * x_19;
    root = ((struct Tree*)(Tiger_new(&Tree_vtable_, sizeof(struct Tree))));
    ntb = (x_1=root, x_1->vptr->Init(x_1, 16));
    ntb = (x_2=root, x_2->vptr->Print(x_2));
    System_out_println(100000000);
    ntb = (x_3=root, x_3->vptr->Insert(x_3, 8));
    ntb = (x_4=root, x_4->vptr->Insert(x_4, 24));
    ntb = (x_5=root, x_5->vptr->Insert(x_5, 4));
    ntb = (x_6=root, x_6->vptr->Insert(x_6, 12));
    ntb = (x_7=root, x_7->vptr->Insert(x_7, 20));
    ntb = (x_8=root, x_8->vptr->Insert(x_8, 28));
    ntb = (x_9=root, x_9->vptr->Insert(x_9, 14));
    ntb = (x_10=root, x_10->vptr->Print(x_10));
    System_out_println(100000000);
    v = ((struct MyVisitor*)(Tiger_new(&MyVisitor_vtable_, sizeof(struct MyVisitor))));
    System_out_println(50000000);
    nti = (x_11=root, x_11->vptr->accept(x_11, (struct Visitor *)v));
    System_out_println(100000000);
    System_out_println((x_12=root, x_12->vptr->Search(x_12, 24)));
    System_out_println((x_13=root, x_13->vptr->Search(x_13, 12)));
    System_out_println((x_14=root, x_14->vptr->Search(x_14, 16)));
    System_out_println((x_15=root, x_15->vptr->Search(x_15, 50)));
    System_out_println((x_16=root, x_16->vptr->Search(x_16, 12)));
    ntb = (x_17=root, x_17->vptr->Delete(x_17, 12));
    ntb = (x_18=root, x_18->vptr->Print(x_18));
    System_out_println((x_19=root, x_19->vptr->Search(x_19, 12)));
    return 0;
}

int Tree_Init(struct Tree * this, int v_key)
{
    this->key = v_key;
    this->has_left = 0;
    this->has_right = 0;
    return 1;
}

int Tree_SetRight(struct Tree * this, struct Tree * rn)
{
    this->right = rn;
    return 1;
}

int Tree_SetLeft(struct Tree * this, struct Tree * ln)
{
    this->left = ln;
    return 1;
}

struct Tree * Tree_GetRight(struct Tree * this)
{
    return this->right;
}

struct Tree * Tree_GetLeft(struct Tree * this)
{
    return this->left;
}

int Tree_GetKey(struct Tree * this)
{
    return this->key;
}

int Tree_SetKey(struct Tree * this, int v_key)
{
    this->key = v_key;
    return 1;
}

int Tree_GetHas_Right(struct Tree * this)
{
    return this->has_right;
}

int Tree_GetHas_Left(struct Tree * this)
{
    return this->has_left;
}

int Tree_SetHas_Left(struct Tree * this, int val)
{
    this->has_left = val;
    return 1;
}

int Tree_SetHas_Right(struct Tree * this, int val)
{
    this->has_right = val;
    return 1;
}

int Tree_Compare(struct Tree * this, int num1, int num2)
{
    int ntb;
    int nti;
    ntb = 0;
    nti = num2 + 1;
    if(num1 < num2)
        ntb = 0;
    else
        if(!(num1 < nti))
            ntb = 0;
        else
            ntb = 1;
    return ntb;
}

int Tree_Insert(struct Tree * this, int v_key)
{
    struct Tree * new_node;
    int ntb;
    struct Tree * current_node;
    int cont;
    int key_aux;
    struct Tree * x_20;
    struct Tree * x_21;
    struct Tree * x_22;
    struct Tree * x_23;
    struct Tree * x_24;
    struct Tree * x_25;
    struct Tree * x_26;
    struct Tree * x_27;
    struct Tree * x_28;
    struct Tree * x_29;
    new_node = ((struct Tree*)(Tiger_new(&Tree_vtable_, sizeof(struct Tree))));
    ntb = (x_20=new_node, x_20->vptr->Init(x_20, v_key));
    current_node = this;
    cont = 1;
    while(cont)
    {
        key_aux = (x_21=current_node, x_21->vptr->GetKey(x_21));
        if(v_key < key_aux)
        {
            if((x_22=current_node, x_22->vptr->GetHas_Left(x_22)))
                current_node = (x_23=current_node, x_23->vptr->GetLeft(x_23));
            else
            {
                cont = 0;
                ntb = (x_24=current_node, x_24->vptr->SetHas_Left(x_24, 1));
                ntb = (x_25=current_node, x_25->vptr->SetLeft(x_25, (struct Tree *)new_node));
            }
        }
        else
        {
            if((x_26=current_node, x_26->vptr->GetHas_Right(x_26)))
                current_node = (x_27=current_node, x_27->vptr->GetRight(x_27));
            else
            {
                cont = 0;
                ntb = (x_28=current_node, x_28->vptr->SetHas_Right(x_28, 1));
                ntb = (x_29=current_node, x_29->vptr->SetRight(x_29, (struct Tree *)new_node));
            }
        }
    }
    return 1;
}

int Tree_Delete(struct Tree * this, int v_key)
{
    struct Tree * current_node;
    struct Tree * parent_node;
    int cont;
    int found;
    int ntb;
    int is_root;
    int key_aux;
    struct Tree * x_30;
    struct Tree * x_31;
    struct Tree * x_32;
    struct Tree * x_33;
    struct Tree * x_34;
    struct Tree * x_35;
    struct Tree * x_36;
    struct Tree * x_37;
    struct Tree * x_38;
    current_node = this;
    parent_node = this;
    cont = 1;
    found = 0;
    is_root = 1;
    while(cont)
    {
        key_aux = (x_30=current_node, x_30->vptr->GetKey(x_30));
        if(v_key < key_aux)
            if((x_31=current_node, x_31->vptr->GetHas_Left(x_31)))
            {
                parent_node = current_node;
                current_node = (x_32=current_node, x_32->vptr->GetLeft(x_32));
            }
            else
                cont = 0;
        else
            if(key_aux < v_key)
                if((x_33=current_node, x_33->vptr->GetHas_Right(x_33)))
                {
                    parent_node = current_node;
                    current_node = (x_34=current_node, x_34->vptr->GetRight(x_34));
                }
                else
                    cont = 0;
            else
            {
                if(is_root)
                    if(!(x_35=current_node, x_35->vptr->GetHas_Right(x_35)) && !(x_36=current_node, x_36->vptr->GetHas_Left(x_36)))
                        ntb = 1;
                    else
                        ntb = (x_37=this, x_37->vptr->Remove(x_37, (struct Tree *)parent_node, (struct Tree *)current_node));
                else
                    ntb = (x_38=this, x_38->vptr->Remove(x_38, (struct Tree *)parent_node, (struct Tree *)current_node));
                found = 1;
                cont = 0;
            }
        is_root = 0;
    }
    return found;
}

int Tree_Remove(struct Tree * this, struct Tree * p_node, struct Tree * c_node)
{
    int ntb;
    int auxkey1;
    int auxkey2;
    struct Tree * x_39;
    struct Tree * x_40;
    struct Tree * x_41;
    struct Tree * x_42;
    struct Tree * x_43;
    struct Tree * x_44;
    struct Tree * x_45;
    struct Tree * x_46;
    struct Tree * x_47;
    struct Tree * x_48;
    struct Tree * x_49;
    struct Tree * x_50;
    if((x_39=c_node, x_39->vptr->GetHas_Left(x_39)))
        ntb = (x_40=this, x_40->vptr->RemoveLeft(x_40, (struct Tree *)p_node, (struct Tree *)c_node));
    else
        if((x_41=c_node, x_41->vptr->GetHas_Right(x_41)))
            ntb = (x_42=this, x_42->vptr->RemoveRight(x_42, (struct Tree *)p_node, (struct Tree *)c_node));
        else
        {
            auxkey1 = (x_43=c_node, x_43->vptr->GetKey(x_43));
            auxkey2 = (x_45=((x_44=p_node, x_44->vptr->GetLeft(x_44))), x_45->vptr->GetKey(x_45));
            if((x_46=this, x_46->vptr->Compare(x_46, auxkey1, auxkey2)))
            {
                ntb = (x_47=p_node, x_47->vptr->SetLeft(x_47, (struct Tree *)this->my_null));
                ntb = (x_48=p_node, x_48->vptr->SetHas_Left(x_48, 0));
            }
            else
            {
                ntb = (x_49=p_node, x_49->vptr->SetRight(x_49, (struct Tree *)this->my_null));
                ntb = (x_50=p_node, x_50->vptr->SetHas_Right(x_50, 0));
            }
        }
    return 1;
}

int Tree_RemoveRight(struct Tree * this, struct Tree * p_node, struct Tree * c_node)
{
    int ntb;
    struct Tree * x_51;
    struct Tree * x_52;
    struct Tree * x_53;
    struct Tree * x_54;
    struct Tree * x_55;
    struct Tree * x_56;
    struct Tree * x_57;
    while((x_51=c_node, x_51->vptr->GetHas_Right(x_51)))
    {
        ntb = (x_52=c_node, x_52->vptr->SetKey(x_52, (x_54=((x_53=c_node, x_53->vptr->GetRight(x_53))), x_54->vptr->GetKey(x_54))));
        p_node = c_node;
        c_node = (x_55=c_node, x_55->vptr->GetRight(x_55));
    }
    ntb = (x_56=p_node, x_56->vptr->SetRight(x_56, (struct Tree *)this->my_null));
    ntb = (x_57=p_node, x_57->vptr->SetHas_Right(x_57, 0));
    return 1;
}

int Tree_RemoveLeft(struct Tree * this, struct Tree * p_node, struct Tree * c_node)
{
    int ntb;
    struct Tree * x_58;
    struct Tree * x_59;
    struct Tree * x_60;
    struct Tree * x_61;
    struct Tree * x_62;
    struct Tree * x_63;
    struct Tree * x_64;
    while((x_58=c_node, x_58->vptr->GetHas_Left(x_58)))
    {
        ntb = (x_59=c_node, x_59->vptr->SetKey(x_59, (x_61=((x_60=c_node, x_60->vptr->GetLeft(x_60))), x_61->vptr->GetKey(x_61))));
        p_node = c_node;
        c_node = (x_62=c_node, x_62->vptr->GetLeft(x_62));
    }
    ntb = (x_63=p_node, x_63->vptr->SetLeft(x_63, (struct Tree *)this->my_null));
    ntb = (x_64=p_node, x_64->vptr->SetHas_Left(x_64, 0));
    return 1;
}

int Tree_Search(struct Tree * this, int v_key)
{
    struct Tree * current_node;
    int ifound;
    int cont;
    int key_aux;
    struct Tree * x_65;
    struct Tree * x_66;
    struct Tree * x_67;
    struct Tree * x_68;
    struct Tree * x_69;
    current_node = this;
    cont = 1;
    ifound = 0;
    while(cont)
    {
        key_aux = (x_65=current_node, x_65->vptr->GetKey(x_65));
        if(v_key < key_aux)
            if((x_66=current_node, x_66->vptr->GetHas_Left(x_66)))
                current_node = (x_67=current_node, x_67->vptr->GetLeft(x_67));
            else
                cont = 0;
        else
            if(key_aux < v_key)
                if((x_68=current_node, x_68->vptr->GetHas_Right(x_68)))
                    current_node = (x_69=current_node, x_69->vptr->GetRight(x_69));
                else
                    cont = 0;
            else
            {
                ifound = 1;
                cont = 0;
            }
    }
    return ifound;
}

int Tree_Print(struct Tree * this)
{
    int ntb;
    struct Tree * current_node;
    struct Tree * x_70;
    current_node = this;
    ntb = (x_70=this, x_70->vptr->RecPrint(x_70, (struct Tree *)current_node));
    return 1;
}

int Tree_RecPrint(struct Tree * this, struct Tree * node)
{
    int ntb;
    struct Tree * x_71;
    struct Tree * x_72;
    struct Tree * x_73;
    struct Tree * x_74;
    struct Tree * x_75;
    struct Tree * x_76;
    struct Tree * x_77;
    if((x_71=node, x_71->vptr->GetHas_Left(x_71)))
    {
        ntb = (x_72=this, x_72->vptr->RecPrint(x_72, (struct Tree *)(x_73=node, x_73->vptr->GetLeft(x_73))));
    }
    else
        ntb = 1;
    System_out_println((x_74=node, x_74->vptr->GetKey(x_74)));
    if((x_75=node, x_75->vptr->GetHas_Right(x_75)))
    {
        ntb = (x_76=this, x_76->vptr->RecPrint(x_76, (struct Tree *)(x_77=node, x_77->vptr->GetRight(x_77))));
    }
    else
        ntb = 1;
    return 1;
}

int Tree_accept(struct Tree * this, struct Visitor * v)
{
    int nti;
    struct Visitor * x_78;
    System_out_println(333);
    nti = (x_78=v, x_78->vptr->visit(x_78, (struct Tree *)this));
    return 0;
}

int Visitor_visit(struct Visitor * this, struct Tree * n)
{
    int nti;
    struct Tree * x_79;
    struct Tree * x_80;
    struct Tree * x_81;
    struct Tree * x_82;
    struct Tree * x_83;
    struct Tree * x_84;
    if((x_79=n, x_79->vptr->GetHas_Right(x_79)))
    {
        this->r = (x_80=n, x_80->vptr->GetRight(x_80));
        nti = (x_81=this->r, x_81->vptr->accept(x_81, (struct Visitor *)this));
    }
    else
        nti = 0;
    if((x_82=n, x_82->vptr->GetHas_Left(x_82)))
    {
        this->l = (x_83=n, x_83->vptr->GetLeft(x_83));
        nti = (x_84=this->l, x_84->vptr->accept(x_84, (struct Visitor *)this));
    }
    else
        nti = 0;
    return 0;
}

int MyVisitor_visit(struct MyVisitor * this, struct Tree * n)
{
    int nti;
    struct Tree * x_85;
    struct Tree * x_86;
    struct Tree * x_87;
    struct Tree * x_88;
    struct Tree * x_89;
    struct Tree * x_90;
    struct Tree * x_91;
    if((x_85=n, x_85->vptr->GetHas_Right(x_85)))
    {
        this->r = (x_86=n, x_86->vptr->GetRight(x_86));
        nti = (x_87=this->r, x_87->vptr->accept(x_87, (struct Visitor *)this));
    }
    else
        nti = 0;
    System_out_println((x_88=n, x_88->vptr->GetKey(x_88)));
    if((x_89=n, x_89->vptr->GetHas_Left(x_89)))
    {
        this->l = (x_90=n, x_90->vptr->GetLeft(x_90));
        nti = (x_91=this->l, x_91->vptr->accept(x_91, (struct Visitor *)this));
    }
    else
        nti = 0;
    return 0;
}

// main method
int Tiger_main()
{
    struct TV * x_0;
    System_out_println((x_0=((struct TV*)(Tiger_new(&TV_vtable_, sizeof(struct TV)))), x_0->vptr->Start(x_0)));
}

