package com.pyrion.poison_frog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

//from temp
import android.content.Intent;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.Stack;

import androidx.fragment.app.Fragment;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class MainCenterFragment extends Fragment {

    Toast toast;
    Random random = new Random();
    EditText foodInputEditText;
    TextView logTextView, moneyStringTextView;
    StringBuffer logStringBuffer = new StringBuffer();
    String logString = "";
    ImageView mainFrogImageView, chefHatIconView, healthCareIconView;
    View menuIconsView, mainBackgroundView;
    ScrollView logScrollView;
    Stack<String> originFoodNameStack = new Stack<>();
    InputMethodManager imm;
    int frogTouchedCount = 0;
    int currentFrogPrice = 80;
    int currentUserMoney = 0;
    int currentFrogSize = 80;
    int currentPower = 0;
// 못외워서 일단 써둠                    Log.i("tag",foodLogString);


    String[] deadFrogMsgs = new String[]{
            "[개구리는 지금 시체일 뿐이야.]",
            "[죽은 개구리는 대답이없다.]",
            "[개구리 죽었다니까.]",
            "[미련가지지마. 개구리는 죽었어.]",
            "[너는 개구리를 죽게했어.]",
            "[그런다고 개구리가 살아나지는 않아.]",
            "[죽은 개구리는 찔러도 반응이없어.]",
            "[개구리를 다시 살리려면 치료 아이콘 클릭]"
    };
    String[] soledFrogMsgs = new String[]{
            "안녕 주인아...고마웠어",
            "키워줘서 고마웠어.",
            "이제 자유가 되는구나...",
            "나 먼저 간다... 행복해라...",
            "개굴개굴...고기가 되는건가..",
            "안돼...가기싫어...",
            "끄악...개굴!!!",
            "개구리 살려!!!"
    };
    String[] poisonFoods = new String[]{
            "독",
            "모기",
            "소금",
            "해삼",
            "고추",
            "거미",
            "복어",
            "뱀",
            "전갈"
    };

    //clicked settings
    View mainMoneyIconSet;
    ImageView mainHouseIcon, mainFoodIcon, mainDumbbellIcon,  mainGamePlayIcon, mainSellIcon;
    ImageView mainEraserIcon;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        //서버로 부터 데이터 받아서 리스트에 넣는거 넣기
        super.onCreate(savedInstanceState);



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View returnView = inflater.inflate(R.layout.fragment_main_center, container, false);
        return returnView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        foodInputEditText = view.findViewById(R.id.food_name_input);
        logTextView = view.findViewById(R.id.food_log);
        logScrollView = view.findViewById(R.id.log_scroll);
        mainFrogImageView = view.findViewById(R.id.main_frog_icon);
        chefHatIconView = view.findViewById(R.id.chef_hat);
        menuIconsView = view.findViewById(R.id.menu_list);
        mainBackgroundView = view.findViewById(R.id.main_background_clickable);
        healthCareIconView = view.findViewById(R.id.health);
        moneyStringTextView = view.findViewById(R.id.money_string);

        ///icons only for click listener
        mainHouseIcon = view.findViewById(R.id.main_house_icon);
        mainFoodIcon = view.findViewById(R.id.main_food_icon);
        mainDumbbellIcon = view.findViewById(R.id.main_dumbbell_icon);
        mainMoneyIconSet = view.findViewById(R.id.main_money_icon);
        mainGamePlayIcon = view.findViewById(R.id.main_game_play_icon);
        mainSellIcon = view.findViewById(R.id.main_sell_icon);
        mainEraserIcon = view.findViewById(R.id.main_eraser_iocn);

        imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE); //getActivity(). 해줘야함.
        foodInputEditText.setOnEditorActionListener(foodInputActionListener);

        currentUserMoney = Integer.parseInt(moneyStringTextView.getText().toString());


        //click listener settings
        chefHatIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(menuIconsView.getVisibility()==View.VISIBLE){
                    menuIconsView.setVisibility(View.GONE);
                    foodInputEditText.setVisibility(View.VISIBLE);

                    //keyboard popup
                    foodInputEditText.requestFocus();
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    return;
                }
                hideFoodInputEditText();
            }
        });
        healthCareIconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //개구리 되살리기
                if(Frog.currentState==Frog.DEATH){
                    int resurrectionPrice = (int) (currentFrogPrice*0.7);
                    addLogString("[치료비: "+resurrectionPrice+"$]");
                    if(currentUserMoney >=resurrectionPrice){
                        changeCurrentMoney(-resurrectionPrice);
                        resurrection();
                        return;
                    }
                    showToastString("돈 부족");
                    addLogString("[돈이 부족해서 치료를 못했습니다.]");

                }else if(Frog.currentState==Frog.SOLD){
                    showToastString("치료할 개구리가 없음");
                    addLogString("[개구리를 새로 구매해 주세요.]");
                }else{
                    showToastString("건강해서 치료 불필요");
                }
            }
        });

        mainGamePlayIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //game play
            }
        });

        mainSellIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentFrogPrice<40){
                    addLogString("[개구리가 너무 작아서 팔수 없습니다.]");
                    showToastString("판매 실패");
                    return;

                }else if(Frog.currentState==Frog.SOLD){
                    addLogString("[이미 개구리가 팔리고 없습니다.]");
                    showToastString("판매 불가능");
                    return;

                }else if(Frog.currentState==Frog.DEATH) {
                    addLogString("[개구리 시체는 쓸모가 없는데...]");//
                    showToastString("헐값에 판매 완료");
                    changeCurrentMoney(currentFrogPrice / 10);

                }else{
                    changeCurrentMoney(currentFrogPrice);
                    showToastString("판매 완료");
                }

                addLogString(soledFrogMsgs[random.nextInt(soledFrogMsgs.length)]);
                addLogString("[개구리가 판매되었습니다.]");
                updateFrogState(Frog.SOLD);
            }
        });


        mainHouseIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), frogHouseActivity.class);//this가 getActivity()로
                //"퐁퐁Frog", "fire", 80, 80));
                intent.putExtra("name", "Frog Name");
                intent.putExtra("property","fire");
                intent.putExtra("size",currentFrogSize);
                intent.putExtra("power",currentFrogSize);


                //결과 값도 돌려 받을 거임
                startActivityForResult(intent, 999);
            }
        });

        mainFoodIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Frog.currentState==Frog.SOLD){
                    showToastString("밥먹이기 불가능");
                    addLogString("[팔려간 개구리는 밥을 먹지 못함.]");
                    return;
                }
                if(Frog.currentState==Frog.DEATH){
                    showToastString("밥먹이기 불가능");
                    addLogString("[죽은 개구리는 밥을 법지 못함.]");
                    return;
                }
                changeFrogSizePrice(+1);
                showToastString("크기+1");
            }
        });

        mainDumbbellIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToastString("힘+1");
                currentPower+=1;            }
        });


        mainMoneyIconSet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Frog.currentState==Frog.SOLD){
                showToastString("돈벌기 불가능");
                addLogString("[일할 개구리가 없음.]");
                return;
            }
            if(Frog.currentState==Frog.DEATH){
                showToastString("돈벌기 불가능");
                addLogString("[죽은 개구리는 일을 못함.]");
                return;
            }
            changeCurrentMoney(+1);
            showToastString("+1$");
            }
        });


        //TODO 개구리 터치 횟수 카운트 앤 무빙 리액션
        mainFrogImageView.setOnClickListener(new View.OnClickListener() {
            @Override
             public void onClick(View v) {
                if (Frog.currentState == Frog.SOLD) {
                    if (currentUserMoney < 100) {
                        showToastString("구매 불가능");
                        addLogString("[돈이 부족하네..]");
                        return;
                    }
                    addLogString("[개구리를 새로 구매 하셨습니다.]");
                    changeCurrentMoney(-100);
                    buyNewFrog();
                    return;
                }


                if (Frog.currentState == Frog.DEATH) {
                    addLogString(deadFrogMsgs[random.nextInt(8)]);
                    return;
                }
                frogTouchedCount++;
                showToastString("개구리 찌름");
                if (logStringBuffer.length() == 0) {
                    logTextView.setText("");
                }
                switch (frogTouchedCount) {
                    case 1:
                        addLogString("왜요?");
                        break;
                    case 2:
                        addLogString("잘 살아 있다구요.");
                        break;
                    case 3:
                        addLogString("아파요.");
                        break;
                    case 4:
                        addLogString("힘들어요. 그만 찌르세요");
                        break;
                    case 5:
                        addLogString("죽을 것 같아요.");
                        break;
                    default: {

                        if (random.nextInt(2) == 1) {
                            updateFrogState(Frog.DEATH);
                        } else {
                            addLogString("[개구리 상태가 이상하다.]");
                            frogTouchedCount = random.nextInt(6);
                        }
                    }
                    break;
                }
            }
        });



        mainEraserIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logStringBuffer= new StringBuffer();
                logTextView.setText("Ready...");
                logScrollView.fullScroll(View.FOCUS_DOWN);
            }
        });

        //temp
        setStartFrog();



        mainBackgroundView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideFoodInputEditText();
            }
        });


    }

    EditText.OnEditorActionListener foodInputActionListener = new EditText.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            //들어오는 글자 하나하나 알수있음
            //event.keypad현재 키패드에서 누른 글자 알수있음 한글인지 알아보기// actionID==EditorInfo.IME_ACTION_SEARCH
            //actionID 는 무조건 오른쪽 아래에
            if(event == null){
                //엔터 눌렀을 때
                String newFoodName = v.getText().toString();


                if(newFoodName.length()>0) {
                    if(logStringBuffer.length()==0) {
                        logTextView.setText("");
                    }
                    originFoodNameStack.add(newFoodName);

                    if(Frog.currentState==Frog.DEATH){
                        addLogString("[죽은 개구리는 "+newFoodName+" 먹지 못함.]");
                        showToastString("반응 없음");
                        hideFoodInputEditText();
                        return false;
                    }
                    if(Frog.currentState==Frog.SOLD){
                        addLogString("[팔려간 개구리는 "+newFoodName+" 먹지 못함.]");
                        showToastString("개구리 없음");
                        hideFoodInputEditText();
                        return false;
                    }
                    showToastString("개구리가 "+newFoodName+" 먹음");
                    int randomFoodPoint = random.nextInt(10)-1;//-1~9


                    for(String poisonFood: poisonFoods){
                        if(newFoodName.equals(poisonFood)){
                            addLogString("커어어억... 이 맛은!!!");
                            if(random.nextBoolean()){
                                changeFrogSizePrice(randomFoodPoint *30);
                                addLogString("[개구리가 급격히 성장 함]");
                            }else{
                                updateFrogState(Frog.DEATH);
                            }

                            hideFoodInputEditText();
                            return false;
                        }
                    }

                    changeFrogSizePrice(randomFoodPoint);
                    showNewFoodLogSet(newFoodName, randomFoodPoint);
                    v.setText("");
                }
                hideFoodInputEditText();
            }
            return false;
        }

    };


    void showNewFoodLogSet(String newFoodName, int like){
        if(like < 1){
            addLogString("[맛없어서 개구리가 작아짐]");
        }
        addLogString("[먹인 음식: "+newFoodName+"]");
        addLogString("[_씹"+"는 중...]");
        addLogString("옴뇸뇸뇸"+"_맛있는 "+ newFoodName+"입니다.");
        addLogString("_건강해지는"+"맛이예요.");
        addLogString("방금 먹은 음식과"+"_잘 안"+ "어울리네요.");
        addLogString("또 먹고싶" +"_지 않아"+"요.");
        addLogString("짜증나"+"요.");
    }



    public void setStartFrog() {
        updateFrogState(Frog.ALIVE);
        //앱 켰을 때 개구리 셋팅

        if(Frog.currentState== Frog.ALIVE){

        }

        //db에 최근 개구리가 있으면 그거 띄우기

        //최근 개구리가 없을 경우 새 개구리를 사기.
    }



    public void showToastString(String text){
        if(toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(
                getActivity(),
                text,
                Toast.LENGTH_SHORT
        );
        toast.setGravity(Gravity.CENTER_VERTICAL, 0 , 200);
        toast.show();
    }


    public void hideFoodInputEditText() {
        menuIconsView.setVisibility(View.VISIBLE);
        foodInputEditText.setVisibility(View.GONE);
        foodInputEditText.setText("");
        imm.hideSoftInputFromWindow(foodInputEditText.getWindowToken(), 0);
    }

    public void addLogString(String string){
        logStringBuffer.append(string + "\n\n");
        logTextView.setText(logStringBuffer.toString());
        logScrollView.fullScroll(View.FOCUS_DOWN);
    }

    void resurrection(){
        addLogString("[개구리가 치료되었습니다.]");
        if(random.nextBoolean()){
            addLogString("죽었다가 살아났습니다.");
        }else{
            addLogString("살려줘서 고맙다 개굴.");
        }
        showToastString("개구리 부활");
        updateFrogState(Frog.ALIVE);
        mainFrogImageView.setImageResource(R.drawable.main_frog_jelly);
    }

    public void changeCurrentMoney(int diff){
        currentUserMoney += diff;
        moneyStringTextView.setText((currentUserMoney)+"");
    }


    public void changeFrogSizePrice(int diff) {
        currentFrogSize += diff;
        currentFrogPrice += diff;
        mainFrogImageView.getLayoutParams().height=currentFrogSize;
        mainFrogImageView.getLayoutParams().width=currentFrogSize;
        mainFrogImageView.requestLayout();
    }




    private void buyNewFrog() {
        //TODO 새로 사는 개구리 이름 바꾸게 하기
        currentFrogPrice = 80;
        currentFrogSize = 80;
        updateFrogState(Frog.ALIVE);
    }

    public void updateFrogState(int frogState){
        switch (frogState){
            case Frog.ALIVE:
                Frog.currentState = Frog.ALIVE;
                mainFrogImageView.setImageResource(R.drawable.main_frog_jelly);
                mainFrogImageView.getLayoutParams().height=currentFrogSize;
                mainFrogImageView.getLayoutParams().width=currentFrogSize;
                break;
            case Frog.SOLD:
                Frog.currentState = Frog.SOLD;
                mainFrogImageView.setImageResource(R.drawable.main_gift);
                mainFrogImageView.getLayoutParams().height=160;
                mainFrogImageView.getLayoutParams().width=160;
                break;
            case Frog.DEATH:
                Frog.currentState = Frog.DEATH;
                mainFrogImageView.setImageResource(R.drawable.main_dead_frog);
                frogTouchedCount = 0;
                addLogString("[개구리 죽음]");
                showToastString("개구리 사망");
                break;
        }
        mainFrogImageView.requestLayout();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if( requestCode == 999){
            showToastString("yes");
        }

    }


    //TODO 개구리 롱프래스 하면 개구리 상태 보기, 지금 상대와 거래하기
}
