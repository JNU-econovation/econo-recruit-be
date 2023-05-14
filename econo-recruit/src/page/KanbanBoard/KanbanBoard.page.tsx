import { useParams } from 'react-router';
import KanbanNavbarComponent from '@/components/KanbanBoard/Navbar.components';
import KanbanRowComponent from '@/components/KanbanBoard/Row.components';
import KanbanAddRowComponent from '@/components/KanbanBoard/AddRow.component';
import { DragDropContext, DropResult, Droppable } from '@hello-pangea/dnd';
import {
  KanbanDataArrayState,
  KanbanRowData,
} from '@/storage/KanbanBoard/Kanban.atoms';
import CommonNavbarComponent from '@/components/Common/Navbar.component';
import { useAtom } from 'jotai';

const KanbanBoardPage = () => {
  const { period } = useParams();
  const [kanbanData, setKanbanData] = useAtom(KanbanDataArrayState);

  const onDragEnd = (result: DropResult) => {
    if (!result.destination) return;

    if (result.type === 'COLUMN') {
      const toIndex = result.destination.index;
      const fromIndex = result.source.index;
      if (toIndex === fromIndex) return;

      const shallow = JSON.parse(JSON.stringify(kanbanData)) as KanbanRowData[];
      const pickData = shallow.splice(fromIndex, 1);
      shallow.splice(toIndex, 0, ...pickData);

      setKanbanData(shallow);
      return;
    }

    if (result.type === 'DEFAULT') {
      const from = result.source;
      const to = result.destination;

      const shallow = JSON.parse(JSON.stringify(kanbanData)) as KanbanRowData[];
      const pickData = shallow[+from.droppableId].column.splice(from.index, 1);
      shallow[+to.droppableId].column.splice(to.index, 0, ...pickData);

      setKanbanData(shallow);
      return;
    }
  };

  return (
    <div className="pt-12 pl-24 w-screen h-screen flex">
      <CommonNavbarComponent isShort={true} />
      <div className="ml-32 overflow-auto">
        <div className="pt-12 pb-6 text-3xl font-bold border-b-4">
          {period}기 신입모집
        </div>
        <div className="flex gap-4 py-6 font-medium overflow-auto">
          <KanbanNavbarComponent />
        </div>
        <div className="overflow-auto max-h-[calc(100vh-18rem)]">
          <DragDropContext onDragEnd={onDragEnd}>
            <Droppable
              droppableId="droppable"
              type="COLUMN"
              direction="horizontal"
            >
              {(provided) => (
                <div
                  className="flex gap-4"
                  ref={provided.innerRef}
                  {...provided.droppableProps}
                >
                  {kanbanData.map((row, index) => (
                    <KanbanRowComponent
                      index={index}
                      columnData={row.column}
                      columnCount={row.column.length}
                      title={row.title}
                      key={index}
                    />
                  ))}
                  <KanbanAddRowComponent AddRowCallBack={() => ''} />
                  {provided.placeholder}
                </div>
              )}
            </Droppable>
          </DragDropContext>
        </div>
      </div>
    </div>
  );
};

export default KanbanBoardPage;