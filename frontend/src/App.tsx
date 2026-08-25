import { createBrowserRouter, RouterProvider } from "react-router-dom";

import AppLayout from "./components/AppLayout";
import DashboardPage from "./pages/DashboardPage";
import MealsPage from "./pages/MealsPage";
import StudentsPage from "./pages/StudentsPage";
import StudentDetailsPage from "./pages/StudentDetailsPage";
import ClassroomsPage from "./pages/ClassroomsPage";
import GuardiansPage from "./pages/GuardiansPage";
import StaffPage from "./pages/StaffPage";

const router = createBrowserRouter([
  {
    path: "/",
    element: <AppLayout />,
    children: [
      {
        index: true,
        element: <DashboardPage />,
      },
      {
        path: "meals",
        element: <MealsPage />,
      },
      {
        path: "students",
        element: <StudentsPage />,
      },
      {
        path: "students/:studentId",
        element: <StudentDetailsPage />,
      },
      {
        path: "classrooms",
        element: <ClassroomsPage />,
      },
      {
        path: "guardians",
        element: <GuardiansPage />,
      },
      {
        path: "staff",
        element: <StaffPage />,
      },
    ],
  },
]);

export default function App() {
  return <RouterProvider router={router} />;
}