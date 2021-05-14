package edu.iis.mto.oven;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class OvenTest {

    @Mock
    Fan fanMock;

    @Mock
    HeatingModule heatingModuleMock;


    BakingProgram bakingProgram;
    ArrayList <ProgramStage> stages = new ArrayList<>();
    Oven oven;

    @BeforeEach
    void setup() {
        oven = new Oven(heatingModuleMock, fanMock);
    }

    @Test
    void heatingExceptionShouldResultInOvenException() throws Exception{
        ProgramStage stageStub = ProgramStage.builder().withHeat(HeatType.THERMO_CIRCULATION).withStageTime(140).withTargetTemp(270).build();
        stages.add(stageStub);
        bakingProgram = BakingProgram.builder().withInitialTemp(0).withStages(stages).build();
        Assertions.assertNotNull(stageStub);
        Mockito.doNothing().when(fanMock).on();
        Mockito.doThrow(new HeatingException()).when(heatingModuleMock).termalCircuit(any());
        Assertions.assertThrows(OvenException.class,()->{
            oven.start(bakingProgram);
        });

    }

    @Test
    void ifHeatTypeIsNotThermoCirculationFanOnShouldNotBeCalled(){
        ProgramStage stageStub = ProgramStage.builder().withHeat(HeatType.GRILL).withStageTime(80).withTargetTemp(270).build();
        stages.add(stageStub);
        bakingProgram = BakingProgram.builder().withInitialTemp(0).withStages(stages).build();
        oven.start(bakingProgram);
        Mockito.verify(fanMock,Mockito.never()).on();

    }

    @Test
    void ifHeatTypeIsThermoCirculationFanOnShouldBeCalledOnce(){
        ProgramStage stageStub = ProgramStage.builder().withHeat(HeatType.THERMO_CIRCULATION).withStageTime(80).withTargetTemp(270).build();
        stages.add(stageStub);
        bakingProgram = BakingProgram.builder().withInitialTemp(0).withStages(stages).build();
        oven.start(bakingProgram);
        Mockito.verify(fanMock,Mockito.times(1)).on();

    }

    @Test
    void fanOffShouldBeCalledOnce(){
        ProgramStage stageStub = ProgramStage.builder().withHeat(HeatType.HEATER).withStageTime(80).withTargetTemp(270).build();
        stages.add(stageStub);
        bakingProgram = BakingProgram.builder().withInitialTemp(0).withStages(stages).build();
        oven.start(bakingProgram);
        Mockito.verify(fanMock,Mockito.times(1)).off();

    }

}
